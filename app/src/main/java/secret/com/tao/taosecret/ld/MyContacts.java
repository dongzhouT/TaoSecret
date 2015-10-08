package secret.com.tao.taosecret.ld;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import secret.com.tao.taosecret.Config;

/**
 * Created by Adminstrator on 2015/9/21.
 */
public class MyContacts {
    public static String getContactsJsonString(Context context){
        Cursor cursor = context.getContentResolver().query(Phone.CONTENT_URI, null, null, null, Phone.DISPLAY_NAME+" collate nocase asc");
        String phoneNum,disp_name;
        JSONArray jsonArray=new JSONArray();
        JSONObject jsonObject;
        while(cursor.moveToNext()){
            phoneNum=cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
            disp_name=cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
            if(phoneNum.contains("+86")){
                phoneNum=phoneNum.replace("+86","");
            }
            if(phoneNum.contains(" ")){
                phoneNum=phoneNum.replaceAll(" ","");
            }
            jsonObject=new JSONObject();
            try {
                jsonObject.put(Config.ACTION_PHONE_MD5,phoneNum);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
//            Log.v("secret", disp_name + ":" + phoneNum);
        }
        return jsonArray.toString();
    }
}
