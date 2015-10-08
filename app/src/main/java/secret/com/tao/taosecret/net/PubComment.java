package secret.com.tao.taosecret.net;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import secret.com.tao.taosecret.Config;

/**
 * Created by Adminstrator on 2015/9/22.
 */
public class PubComment {
    public PubComment(String phoneNum, String token, String content, String msgId, final SuccessCallBack successCallBack, final FailCallBack failCallBack) {
        new NetConnection(Config.SERVER_URL, HttpMethod.POST, new NetConnection.SuccessCallback() {

            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject obj = new JSONObject(result);
                    switch (obj.getInt(Config.KEY_STATUS)) {
                        case Config.RESULT_STATUS_SUCCESS:
                            if (successCallBack != null) {
                                successCallBack.onSuccess();
                            }
                            break;
                        case Config.RESULT_STATUS_INVALID_TOKEN:
                            if (failCallBack != null) {
                                failCallBack.onFail(Config.RESULT_STATUS_INVALID_TOKEN);
                            }
                            break;
                        default:
                            if (failCallBack != null) {
                                failCallBack.onFail(Config.RESULT_STATUS_FAIL);
                            }
                            break;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (failCallBack != null) {
                        failCallBack.onFail(Config.RESULT_STATUS_FAIL);
                    }
                }

            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail() {
                if (failCallBack != null) {
                    failCallBack.onFail(Config.RESULT_STATUS_FAIL);
                }
            }
        }, Config.KEY_ACTION, Config.ACTION_PUB_COMMENT,
                Config.KEY_PHONE_NUM, phoneNum,
                Config.KEY_TOKEN, token,
                Config.KEY_CONTENT, content,
                Config.KEY_MSG_ID, msgId);

    }

    public interface SuccessCallBack {
        void onSuccess();
    }

    public interface FailCallBack {
        void onFail(int errorCode);
    }
}
