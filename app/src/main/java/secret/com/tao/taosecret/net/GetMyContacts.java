package secret.com.tao.taosecret.net;

import android.content.SyncStatusObserver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import secret.com.tao.taosecret.Config;
import secret.com.tao.taosecret.bean.User;

/**
 * Created by Adminstrator on 2015/9/29.
 */
public class GetMyContacts {
    public GetMyContacts(String phoneNum, String token, final SuccessCallback successCallback, final FailCallback failCallback) {
        new NetConnection(Config.SERVER_URL, HttpMethod.POST, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj=new JSONObject(result);
                    switch (obj.getInt(Config.KEY_STATUS)){
                        case Config.RESULT_STATUS_SUCCESS:
                            if(successCallback!=null) {
                                JSONArray array = obj.getJSONArray(Config.KEY_MY_CONTACTS);
                                List<User> userList = new ArrayList<User>();
                                JSONObject userObj;
                                for (int i = 0; i < array.length(); i++) {
                                    userObj = array.getJSONObject(i);
                                    String phoneNum = userObj.getString(Config.KEY_PHONE_NUM);
                                    Timestamp lastTime = Timestamp.valueOf(userObj.getString(Config.KEY_LASTTIME));
                                    userList.add(new User(phoneNum, null, null, lastTime, null, null));
                                }
                                successCallback.onSuccess(userList);
                            }

                            break;
                        case Config.RESULT_STATUS_INVALID_TOKEN:
                            if(failCallback!=null){
                                failCallback.onFail(Config.RESULT_STATUS_INVALID_TOKEN);
                            }
                            break;
                        default:
                            if(failCallback!=null){
                                failCallback.onFail(Config.RESULT_STATUS_FAIL);
                            }
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if(failCallback!=null){
                        failCallback.onFail(Config.RESULT_STATUS_FAIL);
                    }
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail() {
                if(failCallback!=null){
                    failCallback.onFail(Config.RESULT_STATUS_FAIL);
                }

            }
        }, Config.KEY_ACTION, Config.ACTION_GET_MYCONTACTS,
                Config.KEY_PHONE_NUM, phoneNum,
                Config.KEY_TOKEN, token);

    }

    public interface SuccessCallback {
        void onSuccess(List<User> userList);
    }

    public interface FailCallback {
        void onFail(int errorCode);
    }
}
