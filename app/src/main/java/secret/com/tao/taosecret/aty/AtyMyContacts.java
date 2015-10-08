package secret.com.tao.taosecret.aty;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import secret.com.tao.taosecret.Config;
import secret.com.tao.taosecret.R;
import secret.com.tao.taosecret.adapter.AtyMyContactsAdapter;
import secret.com.tao.taosecret.bean.User;
import secret.com.tao.taosecret.net.GetMyContacts;

/**
 * Created by Adminstrator on 2015/9/29.
 */
public class AtyMyContacts extends Activity {
    private Context mContext = this;
    private String token, phoneNum;
    private List<User> mUserList;
    private ListView lv_my_contacts;
    private AtyMyContactsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_mycontacts);
        lv_my_contacts = (ListView) findViewById(R.id.lv_my_contacts);
        mAdapter = new AtyMyContactsAdapter(this);
        lv_my_contacts.setAdapter(mAdapter);
        Intent intent = getIntent();
        phoneNum = intent.getStringExtra(Config.KEY_PHONE_NUM);
        token = intent.getStringExtra(Config.KEY_TOKEN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getContacts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_aty_mycontacts,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_mycontact_refresh:
                getContacts();
                break;
        }
        return true;
    }

    public void getContacts() {
        final ProgressDialog pd = ProgressDialog.show(mContext, getString(R.string.connecting), getString(R.string.connecting_to_server));
        new GetMyContacts(phoneNum, token, new GetMyContacts.SuccessCallback() {
            @Override
            public void onSuccess(List<User> userList) {
                pd.dismiss();
                mAdapter.clear();
                mAdapter.setUserList(userList);
            }
        }, new GetMyContacts.FailCallback() {
            @Override
            public void onFail(int errorCode) {
                pd.dismiss();
                if (errorCode == Config.RESULT_STATUS_INVALID_TOKEN) {
                    startActivity(new Intent(mContext, AtyLogin.class));
                    Toast.makeText(mContext, getString(R.string.miss_token), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, getString(R.string.fail_to_load_message), Toast.LENGTH_LONG).show();
                }
            }

        });
    }
}
