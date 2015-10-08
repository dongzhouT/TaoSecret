package secret.com.tao.taosecret.aty;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import secret.com.tao.taosecret.Config;
import secret.com.tao.taosecret.R;
import secret.com.tao.taosecret.net.GetCode;
import secret.com.tao.taosecret.net.Login;

/**
 * Created by Adminstrator on 2015/9/21.
 */
public class AtyLogin extends Activity {
    Context mContext;
    private EditText etPhoneNum, etCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_login);
        mContext = this;
        etPhoneNum = (EditText) findViewById(R.id.etPhoneNum);
        etCode = (EditText) findViewById(R.id.etCode);
        //获取验证码
        findViewById(R.id.btnGetCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etPhoneNum.getText().toString();
                if ("".equals(phone)) {
                    Toast.makeText(AtyLogin.this, R.string.phone_not_be_empty, Toast.LENGTH_LONG).show();
                    return;
                }
                final ProgressDialog pd = ProgressDialog.show(mContext, getString(R.string.connecting), getString(R.string.connecting_to_server));
                new GetCode(phone, new GetCode.SuccessCallBack() {
                    @Override
                    public void onSuccess() {
                        pd.dismiss();
                        Toast.makeText(AtyLogin.this, R.string.phone_succ, Toast.LENGTH_LONG).show();
                    }
                }, new GetCode.FailCallBack() {
                    @Override
                    public void onFail() {
                        pd.dismiss();
                        Toast.makeText(AtyLogin.this, R.string.phone_fail, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        //点击登录按钮
        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(etPhoneNum.getText().toString())) {
                    Toast.makeText(AtyLogin.this, R.string.phone_not_be_empty, Toast.LENGTH_LONG).show();
                    return;
                }
                if ("".equals(etCode.getText().toString())) {
                    Toast.makeText(AtyLogin.this, R.string.code_not_be_empty, Toast.LENGTH_LONG).show();
                    return;
                }
                final ProgressDialog pd = ProgressDialog.show(mContext, getString(R.string.connecting), getString(R.string.connecting_to_server));
                pd.setCancelable(true);
                new Login(etPhoneNum.getText().toString(), etCode.getText().toString(), new Login.SuccessCallBack() {
                    @Override
                    public void onSuccess(String token) {
                        if (pd.isShowing())
                            pd.dismiss();
                        Config.cacheToken(mContext, token);
                        Config.cachePhoneNum(mContext, etPhoneNum.getText().toString());
                        Intent i = new Intent(mContext, AtyTimeLine.class);
                        i.putExtra(Config.KEY_TOKEN, token);
                        i.putExtra(Config.KEY_PHONE_NUM, etPhoneNum.getText().toString());
                        startActivity(i);
                        finish();
                    }
                }, new Login.FailCallBack() {
                    @Override
                    public void onFail(int errorCode) {
                        if (pd.isShowing())
                            pd.dismiss();
                        switch (errorCode) {
                            case Config.RESULT_STATUS_INVALID_CODE:
                                Toast.makeText(AtyLogin.this, getString(R.string.code_is_error), Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Config.cacheToken(mContext, null);
                                Toast.makeText(AtyLogin.this, R.string.login_error, Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
            }
        });
    }

    //发送邮件
    public void mailToTao(View v) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + Config.DEVELOPER_TAO));
        startActivity(intent);
    }
}
