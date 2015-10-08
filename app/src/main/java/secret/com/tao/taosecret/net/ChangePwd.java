package secret.com.tao.taosecret.net;

import org.json.JSONException;
import org.json.JSONObject;

import secret.com.tao.taosecret.Config;

/**
 * Created by Adminstrator on 2015/9/29.
 */
public class ChangePwd {
    public ChangePwd(String phoneNum, String token, String oldcode, String code, final SuccessCallback successCallback, final FailCallback failCallback) {
        new NetConnection(Config.SERVER_URL, HttpMethod.POST, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                if (successCallback != null) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        switch (obj.getInt(Config.KEY_STATUS)) {
                            case Config.RESULT_STATUS_SUCCESS:
                                successCallback.onSuccess();
                                break;
                            case Config.RESULT_STATUS_FAIL:
                            case Config.RESULT_STATUS_INVALID_TOKEN:
                            case Config.RESULT_STATUS_INVALID_CODE:
                                if (failCallback != null) {
                                    failCallback.onFail(obj.getInt(Config.KEY_STATUS));
                                }
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (failCallback != null) {
                            failCallback.onFail(Config.RESULT_STATUS_FAIL);
                        }
                    }
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail() {
                if (failCallback != null) {
                    failCallback.onFail(Config.RESULT_STATUS_FAIL);
                }
            }
        }, Config.KEY_ACTION, Config.ACTION_CHANGE_CODE,
                Config.KEY_PHONE_NUM, phoneNum,
                Config.KEY_TOKEN, token,
                Config.KEY_OLD_CODE, oldcode,
                Config.KEY_CODE, code);
    }

    public interface SuccessCallback {
        void onSuccess();
    }

    public interface FailCallback {
        void onFail(int errorCode);
    }
}
