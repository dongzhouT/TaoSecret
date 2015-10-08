package secret.com.tao.taosecret.net;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import secret.com.tao.taosecret.Config;

/**
 * Created by Adminstrator on 2015/9/22.
 * 呈现评论列表界面逻辑的实现
 */
public class GetComment {
    /**
     *
     * @param phoneNum
     * @param token
     * @param page
     * @param perpage
     * @param msgId
     * @param successCallBack
     * @param failCallBack
     */
    public GetComment(String phoneNum, String token, int page, int perpage, String msgId, final SuccessCallBack successCallBack, final FailCallBack failCallBack) {
        new NetConnection(Config.SERVER_URL, HttpMethod.POST, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    switch (obj.getInt(Config.KEY_STATUS)) {
                        case Config.RESULT_STATUS_SUCCESS:
                            if(successCallBack!=null){

                                int c_page=obj.getInt(Config.KEY_PAGE);
                                int c_perpage=obj.getInt(Config.KEY_PERPAGE);
                                String c_msgId=obj.getString(Config.KEY_MSG_ID);
                                JSONArray commentJsonArray=obj.getJSONArray(Config.KEY_COMMENTS);
                                JSONObject commentObj;

                                List<Comment> comments=new ArrayList<Comment>();

                                for(int i=0;i<commentJsonArray.length();i++){
                                    commentObj=commentJsonArray.getJSONObject(i);
                                    String content=commentObj.getString(Config.KEY_CONTENT);
                                    String phoneNum=commentObj.getString(Config.KEY_PHONE_NUM);
                                    String commentTime=commentObj.getString(Config.KEY_COMMENT_TIME);
                                    commentTime=Config.date2zh(commentTime);
                                    Comment comment=new Comment(content,phoneNum,commentTime);
                                    comments.add(comment);
                                }
                                successCallBack.onSuccess(c_page,c_perpage,comments,c_msgId);
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
        }, Config.KEY_ACTION, Config.ACTION_GET_COMMENT,
                Config.KEY_PHONE_NUM, phoneNum,
                Config.KEY_TOKEN, token,
                Config.KEY_PAGE, page + "",
                Config.KEY_PERPAGE, perpage + "",
                Config.KEY_MSG_ID, msgId);

    }

    public interface SuccessCallBack {
        void onSuccess(int page,int perpage,List<Comment> comments,String msgId);
    }

    public interface FailCallBack {
        void onFail(int errorCode);
    }
}
