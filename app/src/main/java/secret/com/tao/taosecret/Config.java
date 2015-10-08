package secret.com.tao.taosecret;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Adminstrator on 2015/9/21.
 */
public class Config {
    //    public static final String SERVER_URL="http://192.168.204.147/php_test/this.php";
    public static final String SERVER_URL = "http://192.168.204.147:8080/SecretServer/";
    //    public static final String SERVER_URL="http://192.168.42.167:8080/SecretServer/";
//    public static final String SERVER_URL = "http://192.168.1.100:8080/SecretServer/";
    public static final String PACKAGENAME = "com.tao.secret";
    public static final String KEY_TOKEN = "token";
    public static final String CHARSET = "UTF-8";
    public static final String KEY_ACTION = "action";
    public static final String KEY_CODE = "code";
    public static final String KEY_STATUS = "status";
    public static final String KEY_PHONE_NUM = "phoneNum";
    public static final String KEY_CONTACTS = "contacts";
    public static final String KEY_PAGE = "page";
    public static final String KEY_PERPAGE = "perpage";
    public static final String KEY_UPLOAD_CONTACTS = "upload_contacts";
    public static final String KEY_TIMELINE = "timeline";
    public static final String KEY_MSG_ID = "msgid";
    public static final String KEY_MSG_TIME = "msgtime";
    public static final String KEY_MSG = "msg";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_COMMENT_TIME = "cmttime";
    public static final String KEY_MY_CONTACTS ="contacts" ;
    public static final String KEY_COMMENT_COUNT ="commentCount" ;
    public static final String KEY_LASTTIME = "lastTime";
    public static final String KEY_OLD_CODE ="oldCode";
    public static final String ACTION_GET_CODE = "send_pass";
    public static final String ACTION_LOGIN = "login";
    public static final String ACTION_PHONE = "phone";
    public static final String ACTION_TIMELINE = "timeline";
    public static final String ACTION_PHONE_MD5 = "phone_md5";
    public static final String ACTION_GET_COMMENT = "get_comment";
    public static final String ACTION_PUB_COMMENT = "pub_comment";
    public static final String ACTION_PUBLISH = "publish";
    public static final String ACTION_GET_MYCONTACTS = "get_contacts";
    public static final String ACTION_CHANGE_CODE = "change_code";
    public static final int RESULT_STATUS_SUCCESS = 1;
    public static final int RESULT_STATUS_FAIL = 0;
    public static final int RESULT_STATUS_INVALID_TOKEN = 2;
    public static final int RESULT_STATUS_INVALID_CODE = 3;
    public static final int ATY_RESULT_NEED_REFRESH = 10000;
    public static final int ATY_RESULT_FROM_COMMENT_NEED_REFRESH = 10001;
    public static final String SHARED_TIMELINE = "timeline";

    public static final String DEVELOPER_TAO="taodongzhou@163.com";


    public static String getToken(Context context) {
        return context.getSharedPreferences(PACKAGENAME, Context.MODE_PRIVATE).getString(KEY_TOKEN, null);
    }

    public static void cacheToken(Context context, String value) {
        SharedPreferences.Editor edit = context.getSharedPreferences(PACKAGENAME, Context.MODE_PRIVATE).edit();
        edit.putString(KEY_TOKEN, value);
        edit.commit();
    }

    public static String getCachedPhoneNum(Context context) {
        return context.getSharedPreferences(PACKAGENAME, Context.MODE_PRIVATE).getString(KEY_PHONE_NUM, null);

    }

    public static void cachePhoneNum(Context context, String phoneNum) {
        SharedPreferences.Editor edit = context.getSharedPreferences(PACKAGENAME, Context.MODE_PRIVATE).edit();
        edit.putString(KEY_PHONE_NUM, phoneNum);
        edit.commit();
    }

    public  static  void save(Context context,String key,Object value ){
        SharedPreferences.Editor edit = context.getSharedPreferences(PACKAGENAME, Context.MODE_PRIVATE).edit();
        if(value instanceof String){
            edit.putString(key, (String) value).commit();
        }else if(value instanceof  Integer){
            edit.putInt(key,(Integer)value).commit();
        }else if(value instanceof Boolean){
            edit.putBoolean(key,(Boolean)value).commit();
        }
    }
    public static String getData(Context context,String key){
        return context.getSharedPreferences(PACKAGENAME,Context.MODE_PRIVATE).getString(key,"");
    }
    public static String date2zh(String dateTime) {

        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfMD = new SimpleDateFormat("MM-dd");
        SimpleDateFormat sdfT = new SimpleDateFormat("HH:mm:ss");
        String dateStr = "", timeStr = "";
        try {
            long dateNow = sdfYMD.parse(sdfYMD.format(new Date()))
                    .getTime();
            Date date = sdf.parse(dateTime);
            Date dateD = sdfYMD.parse(dateTime);
            long timeD = dateD.getTime();
            timeStr = sdfT.format(date);
            if (timeD == dateNow) {
                dateStr = "今天";
            } else if (dateNow - timeD == 1000 * 60 * 60 * 24) {
                dateStr = "昨天";
            } else if (dateNow - timeD == 1000 * 60 * 60 * 24 * 2) {
                dateStr = "前天";
            } else {
                dateStr = sdfMD.format(new Date(timeD));
            }
//            System.out.println("dateNow - timeD=" + (dateNow - timeD) / (1000 * 60 * 60));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        result = dateStr + " " + timeStr;
        return result;

    }
    public  static List<String> getData(int page,int perpage){
        List<String> result=new ArrayList<String>();
        for (int i=0;i<perpage;i++){
            result.add("这是记录"+(page*perpage+i));
        }
        return  result;
    }
    //通过手机号查询联系人姓名
    static public String getNameBynum(Context context,String number){
        String name="";
        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor c = context.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if(c.moveToFirst()){
            name=c.getString(0);
        }
        c.close();
        if("".equals(name)){
            name="未知";
            Log.e("listao","未找到的联系人"+number);
        }
        return name;
    }
}
