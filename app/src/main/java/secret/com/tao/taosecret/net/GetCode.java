package secret.com.tao.taosecret.net;

import org.json.JSONException;
import org.json.JSONObject;

import secret.com.tao.taosecret.Config;

/**
 * Created by Adminstrator on 2015/9/21.
 *  获取手机验证码的通讯类
 */
public class GetCode {
    public GetCode(String phone, final SuccessCallBack successCallBack, final FailCallBack failCallBack) {
        new NetConnection(Config.SERVER_URL, HttpMethod.POST, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    switch (jsonObject.getInt(Config.KEY_STATUS)) {
                        case Config.RESULT_STATUS_SUCCESS:
                            if (successCallBack != null) {
                                successCallBack.onSuccess();
                            }
                            break;
                        default:
                            if (failCallBack != null) {
                                failCallBack.onFail();
                            }

                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (failCallBack != null) {
                        failCallBack.onFail();
                    }
                }


            }
        }, new NetConnection.FailCallback() {

            @Override
            public void onFail() {
                if (failCallBack != null) {
                    failCallBack.onFail();
                }

            }
        }, Config.KEY_ACTION, Config.ACTION_GET_CODE, Config.ACTION_PHONE, phone);
    }

    public interface SuccessCallBack {
        void onSuccess();

    }

    public interface FailCallBack {
        void onFail();
    }
}
