package secret.com.tao.taosecret.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import secret.com.tao.taosecret.net.Message;

/**
 * Created by Adminstrator on 2015/9/30.
 */
public class MessageTransfer {
    public static String list2JsonArray(List _list) throws Exception {
        JSONArray array=new JSONArray();
        JSONObject obj;
        for(int i=0;i<_list.size();i++){
            obj=new JSONObject();
            Message m= (Message) _list.get(i);
            obj.put("msgid",m.getMsgId());
            obj.put("phoneNum",m.getPhoneNum());
            obj.put("commentCount",m.getCommentCount());
            obj.put("msg",m.getMsg());
            obj.put("msgtime",m.getMsgtime());
            array.put(obj);
        }
        return array.toString();
    }
    public static List<Message> jsonArray2List(String arrayStr) throws Exception {
        JSONArray array=new JSONArray(arrayStr);
        List<Message> msgList=new ArrayList<Message>();
        JSONObject obj;
        for(int i=0;i<array.length();i++){
            obj=array.getJSONObject(i);
            String msgid=obj.getString("msgid");
            String phoneNum=obj.getString("phoneNum");
            int commentCount=obj.getInt("commentCount");
            String msg=obj.getString("msg");
            String msgtime=obj.getString("msgtime");
            msgList.add(new Message(msg,phoneNum,msgid,msgtime,commentCount));
        }
        return  msgList;
    }
}
