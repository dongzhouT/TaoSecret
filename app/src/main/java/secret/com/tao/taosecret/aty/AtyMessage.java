package secret.com.tao.taosecret.aty;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import secret.com.tao.taosecret.Config;
import secret.com.tao.taosecret.R;
import secret.com.tao.taosecret.net.Comment;
import secret.com.tao.taosecret.net.GetComment;
import secret.com.tao.taosecret.net.Message;
import secret.com.tao.taosecret.net.PubComment;

/**
 * Created by Adminstrator on 2015/9/21.
 */
public class AtyMessage extends ListActivity {
    private Context mContext = this;
    private String msg, msgId,msgtime, phoneNum, token;
    private TextView tv_message,tv_message_time;
    private AtyMessageListAdapter mAdapter;
    private EditText etComment;
    private int page=0,perpage=10;
    private Button btnSendComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_message);
        etComment = (EditText) findViewById(R.id.etComment);
        Intent intent = getIntent();
        Message msgBean= (Message) intent.getSerializableExtra("msg");
        msg=msgBean.getMsg();
        msgId=msgBean.getMsgId();
        msgtime=msgBean.getMsgtime();
        phoneNum=intent.getStringExtra(Config.KEY_PHONE_NUM);
//        msg = intent.getStringExtra(Config.KEY_MSG);
//        msgId = intent.getStringExtra(Config.KEY_MSG_ID);
//        phoneNum = intent.getStringExtra(Config.KEY_PHONE_NUM);
        token = intent.getStringExtra(Config.KEY_TOKEN);
        tv_message = (TextView) findViewById(R.id.tv_message);
        tv_message_time= (TextView) findViewById(R.id.tv_message_time);
        btnSendComment= (Button) findViewById(R.id.btnSendComment);
        tv_message.setText(msg);
        tv_message_time.setText(msgtime);
        mAdapter = new AtyMessageListAdapter(this);
        setListAdapter(mAdapter);
        setResult(Config.ATY_RESULT_FROM_COMMENT_NEED_REFRESH);
        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setTitle("Publish");
        getActionBar().setHomeButtonEnabled(true);


        getComments();

        //点击发布评论按钮
        btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = etComment.getText().toString();
                if ("".equals(comment)) {
                    Toast.makeText(mContext, getString(R.string.comment_cannot_be_empty), Toast.LENGTH_LONG).show();
                    return;
                }
                final ProgressDialog pd = ProgressDialog.show(mContext, getString(R.string.connecting), getString(R.string.connecting_to_server));
                new PubComment(Config.getCachedPhoneNum(mContext), token, comment, msgId, new PubComment.SuccessCallBack() {
                    @Override
                    public void onSuccess() {
                        etComment.setText("");
                        pd.dismiss();
                        getComments();
                    }
                }, new PubComment.FailCallBack() {
                    @Override
                    public void onFail(int errorCode) {
                        pd.dismiss();
                        if (errorCode == Config.RESULT_STATUS_INVALID_TOKEN) {
                            startActivity(new Intent(mContext, AtyLogin.class));
                            finish();
                        } else {
                            Toast.makeText(mContext, getString(R.string.comment_failed), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void getComments() {
        final ProgressDialog pd = ProgressDialog.show(mContext, getString(R.string.connecting), getString(R.string.connecting_to_server));
        new GetComment(phoneNum, token, page, perpage, msgId, new GetComment.SuccessCallBack() {
            @Override
            public void onSuccess(int page, int perpage, List<Comment> comments, String msgId) {
                pd.dismiss();
                mAdapter.clear();
                mAdapter.addAll(comments);
                Toast.makeText(mContext, getString(R.string.load_comment_success), Toast.LENGTH_LONG).show();
            }
        }, new GetComment.FailCallBack() {
            @Override
            public void onFail(int errorCode) {
                pd.dismiss();
                switch (errorCode) {
                    case Config.RESULT_STATUS_INVALID_TOKEN:
                        startActivity(new Intent(mContext, AtyLogin.class));
                        finish();
                        break;
                    default:
                        Toast.makeText(mContext, getString(R.string.fail_to_load_comment), Toast.LENGTH_LONG).show();
                        break;
                }

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
            break;
        }
        return super.onOptionsItemSelected(item);
    }
}
