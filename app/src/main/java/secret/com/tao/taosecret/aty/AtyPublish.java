package secret.com.tao.taosecret.aty;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import secret.com.tao.taosecret.Config;
import secret.com.tao.taosecret.R;
import secret.com.tao.taosecret.net.Publish;

/**
 * Created by Adminstrator on 2015/9/22.
 */
public class AtyPublish extends Activity {
    Context mContext = this;
    EditText et_publish;
    String phoneNum, token;
    Button btn_publish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_publish);
        Intent intent = getIntent();
        phoneNum = intent.getStringExtra(Config.KEY_PHONE_NUM);
        token = intent.getStringExtra(Config.KEY_TOKEN);
        et_publish = (EditText) findViewById(R.id.et_publish);
        btn_publish = (Button) findViewById(R.id.btn_publish);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        btn_publish.setOnClickListener(new PublishListener());
    }

    class PublishListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            publishMethod();
        }
    }

    private void publishMethod() {
        String content = et_publish.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(mContext, getString(R.string.content_cannot_be_empty), Toast.LENGTH_LONG).show();
            return;
        }
        final ProgressDialog pd = ProgressDialog.show(mContext, getString(R.string.connecting), getString(R.string.connecting_to_server));
        new Publish(phoneNum, token, content, new Publish.SuccessCallBack() {
            @Override
            public void onSuccess() {
                pd.dismiss();
                setResult(Config.ATY_RESULT_NEED_REFRESH);
                Toast.makeText(mContext, getString(R.string.publish_message_success), Toast.LENGTH_LONG).show();
                finish();
            }
        }, new Publish.FailCallBack() {
            @Override
            public void onFail(int errorCode) {
                pd.dismiss();
                if (errorCode == Config.RESULT_STATUS_INVALID_TOKEN) {
                    startActivity(new Intent(mContext, AtyLogin.class));
                } else {
                    Toast.makeText(mContext, getString(R.string.publish_message_fail), Toast.LENGTH_LONG).show();
                }

            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
