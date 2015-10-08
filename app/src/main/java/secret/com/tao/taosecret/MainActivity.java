package secret.com.tao.taosecret;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import secret.com.tao.taosecret.aty.AtyLogin;
import secret.com.tao.taosecret.aty.AtyTimeLine;
import secret.com.tao.taosecret.ld.MyContacts;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String token = Config.getToken(this);
        String phoneNum=Config.getCachedPhoneNum(this);
        if (!"".equals(token)&&token != null&&phoneNum!=null) {
            Intent i = new Intent(this, AtyTimeLine.class);
            i.putExtra(Config.KEY_TOKEN,token);
            i.putExtra(Config.KEY_PHONE_NUM,phoneNum);
            startActivity(i);
        } else {
            Intent i = new Intent(this, AtyLogin.class);
            startActivity(i);
        }
        finish();
    }
}
