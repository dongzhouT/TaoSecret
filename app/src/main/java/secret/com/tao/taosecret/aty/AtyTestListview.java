package secret.com.tao.taosecret.aty;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.widget.ArrayAdapter;

import java.util.List;

import secret.com.tao.taosecret.Config;
import secret.com.tao.taosecret.R;
import secret.com.tao.taosecret.myview.ListviewTest;

/**
 * Created by Adminstrator on 2015/9/28.
 */
public class AtyTestListview extends Activity {
    ListviewTest lv_test;
    ArrayAdapter mAdapter;
    int page=1,perpage=20;

    List<String> data;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    List<String> newData= (List<String>) msg.obj;
                    data.addAll(newData);
                    mAdapter.notifyDataSetChanged();
                    lv_test.OnFinish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_testlistview);
        lv_test = (ListviewTest) findViewById(R.id.lv_test);
        data = Config.getData(0, 20);

        mAdapter = new ArrayAdapter<String>(this, R.layout.aty_comment_list_cell, R.id.tv_comment_cell, data);


        lv_test.setOnLoadListener(new ListviewTest.OnLoadListener() {
            @Override
            public void onload() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(3000);
                        Message msg=mHandler.obtainMessage();
                        msg.obj= Config.getData(page++,perpage);
                        mHandler.sendMessage(msg);
                    }
                }).start();

            }
        });
        lv_test.setAdapter(mAdapter);

    }
}
