package secret.com.tao.taosecret.aty;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import secret.com.tao.taosecret.Config;
import secret.com.tao.taosecret.R;
import secret.com.tao.taosecret.bean.MessageTransfer;
import secret.com.tao.taosecret.ld.MyContacts;
import secret.com.tao.taosecret.myview.MyListView;
import secret.com.tao.taosecret.net.ChangePwd;
import secret.com.tao.taosecret.net.Message;
import secret.com.tao.taosecret.net.TimeLine;
import secret.com.tao.taosecret.net.UploadContacts;

/**
 * Created by Adminstrator on 2015/9/21.
 */
public class AtyTimeLine extends Activity {
    private final int LOAD_COMPLETE = 1;
    private final int EXIT = 100;
    private Context mContext = this;
    private String token, phoneNum;
    private AtyTimeLineMessageListAdapter adapter;
    private int page = 0, perpage = 10, tmpPage = 0, maxPage = 0;
    boolean canExit = false;
    MyListView mListview;
    List<Message> allMsg = new ArrayList<Message>();

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case LOAD_COMPLETE:
                    mListview.setMaxPage(1);
                    mListview.onLoadComplete();
                    Toast.makeText(mContext, getString(R.string.no_more_message), Toast.LENGTH_LONG).show();
                    break;
                case EXIT:
                    canExit = false;
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (canExit) {
            finish();
        } else {
            canExit = true;
            Toast.makeText(mContext, getString(R.string.click_again_to_exit), 2000).show();
            mHandler.sendEmptyMessageDelayed(EXIT, 2000);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_timeline);

        phoneNum = getIntent().getStringExtra(Config.KEY_PHONE_NUM);
        token = getIntent().getStringExtra(Config.KEY_TOKEN);
        adapter = new AtyTimeLineMessageListAdapter(mContext);
        mListview = (MyListView) findViewById(R.id.lv_timeline_myListview);
        mListview.setAdapter(adapter);
        mListview.setOnItemClickListener(new MyItemClickListener());

        mListview.setOnRefreshListener(new MyListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                tmpPage = maxPage = 0;
                loadMessage(false, true);
            }
        });
        mListview.setOnLoadListener(new MyListView.OnLoadListener() {
            @Override
            public void onLoad() {
                Log.v("listao", "onLoad~~~~~~~~~~~~~" + "tmpPage:" + tmpPage + ",maxPage:" + maxPage);
                System.out.println("tmpPage:" + tmpPage + ",maxPage:" + maxPage);
                page = ++tmpPage;
                loadMessage(false, false);
            }
        });


        final ProgressDialog pd = ProgressDialog.show(mContext, getString(R.string.connecting), getString(R.string.connecting_to_server));
        //上传联系人数据到服务器
        new UploadContacts(phoneNum, token, MyContacts.getContactsJsonString(this), new UploadContacts.SuccessCallback() {

            @Override
            public void onSuccess() {
                pd.dismiss();
                loadMessage(true);
            }
        }, new UploadContacts.FailCallback() {

            @Override
            public void onFail(int errorCode) {
                pd.dismiss();
                if (errorCode == Config.RESULT_STATUS_INVALID_TOKEN) {
                    Intent i = new Intent(AtyTimeLine.this, AtyLogin.class);
                    startActivity(i);
                    finish();
                    System.out.println("INVALID_TOKEN......");
                } else {
                    loadMessage(true);
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_aty_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuShowAtyPublish:
                Intent i = new Intent(mContext, AtyPublish.class);
                i.putExtra(Config.KEY_PHONE_NUM, phoneNum);
                i.putExtra(Config.KEY_TOKEN, token);
                //接收返回的参数
                startActivityForResult(i, 0);
                break;
            case R.id.menu_timeline_change_password:
                change_pwd();
                break;
            case R.id.menu_timeline_quit:
                quit();
                break;
            case R.id.menu_my_contacts:
                Intent i_contact = new Intent(mContext, AtyMyContacts.class);
                i_contact.putExtra(Config.KEY_PHONE_NUM, phoneNum);
                i_contact.putExtra(Config.KEY_TOKEN, token);
                startActivity(i_contact);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //修改密码
    public void change_pwd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = View.inflate(mContext, R.layout.dialog_change_pwd, null);
        final EditText et_old = (EditText) v.findViewById(R.id.et_changepass_old);
        final EditText et_new1 = (EditText) v.findViewById(R.id.et_changepass_new1);
        final EditText et_new2 = (EditText) v.findViewById(R.id.et_changepass_new2);
        builder.setView(v);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String oldCode = et_old.getText().toString();
                String new1Code = et_new1.getText().toString();
                String new2Code = et_new2.getText().toString();
                if ("".equals(new1Code)) {
                    Toast.makeText(mContext, getString(R.string.code_not_be_empty), Toast.LENGTH_LONG).show();
                    return;
                } else if (!new1Code.equals(new2Code)) {
                    Toast.makeText(mContext, getString(R.string.code_not_same), Toast.LENGTH_LONG).show();
                    return;
                }

                final ProgressDialog pd = ProgressDialog.show(mContext, getString(R.string.connecting), getString(R.string.connecting_to_server));
                new ChangePwd(phoneNum, token, oldCode, new1Code, new ChangePwd.SuccessCallback() {
                    @Override
                    public void onSuccess() {
                        pd.dismiss();
                        startActivity(new Intent(mContext, AtyLogin.class));
                        Toast.makeText(mContext, getString(R.string.login_with_newcode), Toast.LENGTH_LONG).show();
                        finish();
                    }
                }, new ChangePwd.FailCallback() {
                    @Override
                    public void onFail(int errorCode) {
                        pd.dismiss();
                        switch (errorCode) {
                            case Config.RESULT_STATUS_INVALID_TOKEN:
                                startActivity(new Intent(mContext, AtyLogin.class));
                                finish();
                                Toast.makeText(mContext, getString(R.string.miss_token), Toast.LENGTH_LONG).show();
                                break;
                            case Config.RESULT_STATUS_FAIL:
                                Toast.makeText(mContext, getString(R.string.fail_to_load_message), Toast.LENGTH_LONG).show();
                                break;
                            case Config.RESULT_STATUS_INVALID_CODE:
                                Toast.makeText(mContext, getString(R.string.error_old_code), Toast.LENGTH_LONG).show();
                                break;
                        }

                    }
                });
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.create().show();
    }

    public void quit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.read_me)).setMessage(getString(R.string.quit_message));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Config.cacheToken(mContext, "");
                finish();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case Config.ATY_RESULT_NEED_REFRESH:
                System.out.println("load Message......  from publish");
                loadMessage(true);
                break;
            case Config.ATY_RESULT_FROM_COMMENT_NEED_REFRESH:
                System.out.println("load Message......  from comment");
                loadMessage(true);
                break;
            default:
                break;
        }
    }

    //加载消息
    private void loadMessage(final boolean isClear) {
        loadMessage(isClear, false);
    }

    private void loadMessage(final boolean isClear, final boolean isfresh) {
        System.out.println("load Message......");
        ProgressDialog pd = null;
        if (isClear) {
            page = tmpPage = maxPage = 0;
            pd = ProgressDialog.show(mContext, getString(R.string.connecting), getString(R.string.connecting_to_server));
        }
        final ProgressDialog finalPd = pd;
        System.out.println("isClear:" + isClear + ",isfresh:" + isfresh + ",maxPage:" + maxPage);
        if (!isClear && !isfresh && maxPage != 0) {
            mHandler.sendEmptyMessage(LOAD_COMPLETE);
            return;
        }
        new TimeLine(phoneNum, token, page, perpage, new TimeLine.SuccessCallback() {
            @Override
            public void onSuccess(int page, int perPage, List<Message> msgs) {
                if (isClear) {
                    finalPd.dismiss();
                }
                adapter.clear();
                if (isClear) {
                    //重新加载
                    mListview.setMaxPage(0);
                    allMsg = msgs;
                } else {
                    if (isfresh) {
                        //刷新
                        mListview.setMaxPage(0);
                        allMsg = msgs;
                    } else {
                        //加载
                        allMsg.addAll(msgs);
                    }
                }
                if (msgs.size() < perPage) {
                    maxPage = tmpPage;
                }
                doCache();
                adapter.addAll(allMsg);
                if (!isClear) {
                    if (isfresh) {
                        mListview.onRefreshComplete();
                    } else {
                        mListview.onLoadComplete();
                    }
                }

                System.out.println("adapter success  ~~~~~~");
//                Toast.makeText(mContext, getString(R.string.load_message_success), Toast.LENGTH_LONG).show();
            }
        }, new TimeLine.FailCallback() {
            @Override
            public void onFail(int errorCode) {
                if (isClear) {
                    finalPd.dismiss();
                } else {
                    if (isfresh) {
                        mListview.onRefreshComplete();
                    } else {
                        mListview.onLoadComplete();
                    }
                }
                mListview.setMaxPage(1);
                mListview.onLoadComplete();
                if (errorCode == Config.RESULT_STATUS_INVALID_TOKEN) {
                    startActivity(new Intent(mContext, AtyLogin.class));
                    finish();
                    Toast.makeText(mContext, getString(R.string.miss_token), Toast.LENGTH_LONG).show();
                } else {
                    String msgBk = Config.getData(mContext, Config.SHARED_TIMELINE);
                    if (!TextUtils.isEmpty(msgBk)) {
                        try {
                            allMsg = MessageTransfer.jsonArray2List(msgBk);
                            adapter.clear();
                            adapter.addAll(allMsg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(mContext, getString(R.string.fail_to_load_message), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    class MyItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (id < 0) {
                return;
            }
            int realPosition = (int) id;
            Message msg = adapter.getItem(realPosition);
            System.out.println("phone:" + msg.getPhoneNum());
            Intent i = new Intent(AtyTimeLine.this, AtyMessage.class);
            i.putExtra(Config.KEY_TOKEN, token);
            i.putExtra(Config.KEY_PHONE_NUM, phoneNum);
            i.putExtra("msg", msg);
            startActivityForResult(i, 0);
        }
    }

    @Override
    protected void onDestroy() {
        doCache();
        super.onDestroy();
    }

    private void doCache() {
        if (allMsg.size() > 0) {
            try {
                String msgBk = MessageTransfer.list2JsonArray(allMsg);
                Config.save(mContext, Config.SHARED_TIMELINE, msgBk);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
