package secret.com.tao.taosecret.net;

import org.json.JSONException;
import org.json.JSONObject;

import secret.com.tao.taosecret.Config;

/**
 * Created by Adminstrator on 2015/9/21.
 */
public class Login {
    public Login(String phoneMd5,String code, final SuccessCallBack successCallBack, final FailCallBack failCallBack) {
        new NetConnection(Config.SERVER_URL, HttpMethod.POST, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    switch (jsonObject.getInt(Config.KEY_STATUS)){
                        case Config.RESULT_STATUS_SUCCESS:
                            if(successCallBack!=null){
                                successCallBack.onSuccess(jsonObject.getString(Config.KEY_TOKEN));
                            }
                            break;
                        default:
                            if(failCallBack!=null){
                                failCallBack.onFail(jsonObject.getInt(Config.KEY_STATUS));
                            }
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if(failCallBack!=null){
                        failCallBack.onFail(Config.RESULT_STATUS_INVALID_CODE);
                    }
                }

            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail() {
                if(failCallBack!=null){
                    failCallBack.onFail(Config.RESULT_STATUS_FAIL);
                }

            }
        },Config.KEY_ACTION,Config.ACTION_LOGIN,
                Config.KEY_PHONE_NUM,phoneMd5,
                Config.KEY_CODE,code);
    }
    public interface SuccessCallBack {
        void onSuccess(String token);

    }

    public interface FailCallBack {
        void onFail(int errorCode);
    }
}
