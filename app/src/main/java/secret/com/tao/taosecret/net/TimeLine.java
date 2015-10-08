package secret.com.tao.taosecret.net;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import secret.com.tao.taosecret.Config;

/**
 * Created by Adminstrator on 2015/9/22.
 */
public class TimeLine {
    public TimeLine(final String phoneNum, String token, final int page, final int perPage, final SuccessCallback successCallback, final FailCallback failCallback) {
        new NetConnection(Config.SERVER_URL, HttpMethod.POST, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    if(obj.getInt(Config.KEY_STATUS)==Config.RESULT_STATUS_SUCCESS){
                        if (successCallback != null) {
                            List<Message> msgs=new ArrayList<Message>();
                            JSONArray msgJsonArray=obj.getJSONArray(Config.KEY_TIMELINE);
                            JSONObject msgObj;
                            for(int i=0;i<msgJsonArray.length();i++){
                                msgObj=msgJsonArray.getJSONObject(i);
                                String msg=msgObj.getString(Config.KEY_MSG);
                                String msgPhoneNum=msgObj.getString(Config.KEY_PHONE_NUM);
                                String msgId=msgObj.getString(Config.KEY_MSG_ID);
                                String msgtime=msgObj.getString(Config.KEY_MSG_TIME);
                                String countStr=msgObj.getString(Config.KEY_COMMENT_COUNT);
                                int commentCount=countStr==null?0:Integer.parseInt(countStr);
                                msgtime=Config.date2zh(msgtime);
                                //todo 今天，昨天，前天，9-25。。。
//                                SimpleDateFormat sdf=new SimpleDateFormat(msgtime);
                                msgs.add(new Message(msg,msgPhoneNum,msgId,msgtime,commentCount));
                            }
                            successCallback.onSuccess(obj.getInt(Config.KEY_PAGE), obj.getInt(Config.KEY_PERPAGE), msgs);
                        }
                    }else if(obj.getInt(Config.KEY_STATUS)==Config.RESULT_STATUS_INVALID_TOKEN){
                        if (failCallback != null) {
                            failCallback.onFail(Config.RESULT_STATUS_INVALID_TOKEN);
                        }
                    }else{
                        if (failCallback != null) {
                            failCallback.onFail(Config.RESULT_STATUS_FAIL);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    if (failCallback != null) {
                        failCallback.onFail(Config.RESULT_STATUS_FAIL);
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
        }, Config.KEY_ACTION, Config.ACTION_TIMELINE,
                Config.KEY_PHONE_NUM, phoneNum,
                Config.KEY_TOKEN, token,
                Config.KEY_PAGE, page + "",
                Config.KEY_PERPAGE, perPage + "");

    }

    public interface SuccessCallback {
        void onSuccess(int page, int perPage, List<Message> msgs);
    }

    public interface FailCallback {
        void onFail(int errorCode);
    }
}
