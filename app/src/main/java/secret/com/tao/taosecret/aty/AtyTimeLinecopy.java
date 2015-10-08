package secret.com.tao.taosecret.aty;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import secret.com.tao.taosecret.Config;
import secret.com.tao.taosecret.R;
import secret.com.tao.taosecret.ld.MyContacts;
import secret.com.tao.taosecret.net.Message;
import secret.com.tao.taosecret.net.TimeLine;
import secret.com.tao.taosecret.net.UploadContacts;

/**
 * Created by Adminstrator on 2015/9/21.
 */
public class AtyTimeLinecopy extends ListActivity {
    private Context mContext = this;
    private String token, phoneNum;
    private AtyTimeLineMessageListAdapter adapter;
    private int page=0,perpage=10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_timeline);

        phoneNum = getIntent().getStringExtra(Config.KEY_PHONE_NUM);
        token = getIntent().getStringExtra(Config.KEY_TOKEN);
        adapter = new AtyTimeLineMessageListAdapter(mContext);
        setListAdapter(adapter);
        final ProgressDialog pd = ProgressDialog.show(mContext, getString(R.string.connecting), getString(R.string.connecting_to_server));
        //上传联系人数据到服务器
        new UploadContacts(phoneNum, token, MyContacts.getContactsJsonString(this), new UploadContacts.SuccessCallback() {

            @Override
            public void onSuccess() {
                pd.dismiss();
                loadMessage();
            }
        }, new UploadContacts.FailCallback() {

            @Override
            public void onFail(int errorCode) {
                pd.dismiss();
                if (errorCode == Config.RESULT_STATUS_INVALID_TOKEN) {
                    Intent i = new Intent(AtyTimeLinecopy.this, AtyLogin.class);
                    startActivity(i);
                    finish();
                    System.out.println("INVALID_TOKEN......");
                } else {
                    loadMessage();
                }

            }
        });
    }

    @Override
         public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_aty_timeline,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuShowAtyPublish:
                Intent i=new Intent(mContext,AtyPublish.class);
                i.putExtra(Config.KEY_PHONE_NUM,phoneNum);
                i.putExtra(Config.KEY_TOKEN, token);
                //接收返回的参数
                startActivityForResult(i,0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Message msg=adapter.getItem(position);
        Intent i=new Intent(AtyTimeLinecopy.this,AtyMessage.class);
//        i.putExtra(Config.KEY_MSG,msg.getMsg());
//        i.putExtra(Config.KEY_PHONE_NUM,msg.getPhoneNum());
//        i.putExtra(Config.KEY_MSG_ID,msg.getMsgId());
        i.putExtra(Config.KEY_TOKEN, token);
        i.putExtra("msg",msg);
        startActivityForResult(i,0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case Config.ATY_RESULT_NEED_REFRESH:
                System.out.println("load Message......  from publish");
                loadMessage();
                break;
            case Config.ATY_RESULT_FROM_COMMENT_NEED_REFRESH:
                System.out.println("load Message......  from comment");
                loadMessage();
                break;
            default:
                break;
        }
    }

    //加载消息
    private void loadMessage() {
        System.out.println("load Message......");
        final ProgressDialog pd = ProgressDialog.show(mContext, getString(R.string.connecting),getString(R.string.connecting_to_server));
        new TimeLine(phoneNum, token, page, perpage, new TimeLine.SuccessCallback() {
            @Override
            public void onSuccess(int page, int perPage, List<Message> msgs) {
                pd.dismiss();
                adapter.clear();
                adapter.addAll(msgs);
                System.out.println("adapter success  ~~~~~~");
                Toast.makeText(mContext, getString(R.string.load_message_success), Toast.LENGTH_LONG).show();

            }
        }, new TimeLine.FailCallback() {
            @Override
            public void onFail(int errorCode) {
                pd.dismiss();
                if(errorCode==Config.RESULT_STATUS_INVALID_TOKEN){
                    startActivity(new Intent(mContext,AtyLogin.class));
                    Toast.makeText(mContext,getString(R.string.miss_token),Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(mContext,getString(R.string.fail_to_load_message),Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}
