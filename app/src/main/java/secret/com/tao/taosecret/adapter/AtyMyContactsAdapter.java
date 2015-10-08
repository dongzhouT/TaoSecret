package secret.com.tao.taosecret.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import secret.com.tao.taosecret.Config;
import secret.com.tao.taosecret.R;
import secret.com.tao.taosecret.bean.User;

/**
 * Created by Adminstrator on 2015/9/29.
 */
public class AtyMyContactsAdapter extends BaseAdapter {
    private Context mContext;
    private List<User> userList = new ArrayList<User>();

    public AtyMyContactsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }
    public void clear(){
        userList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public User getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (null == convertView) {
            convertView = View.inflate(mContext, R.layout.aty_mycontact_list_cell, null);
            vh = new ViewHolder();
            vh.tv_userName = (TextView) convertView.findViewById(R.id.tv_mycontact_username);
            vh.tv_phoneNum = (TextView) convertView.findViewById(R.id.tv_mycontact_phoneNum);
            vh.tv_lastTime = (TextView) convertView.findViewById(R.id.tv_mycontact_lasttime);
            vh.btn_call= (Button) convertView.findViewById(R.id.cell_mobile_btn);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final User user = getItem(position);
        vh.tv_userName.setText(Config.getNameBynum(mContext, user.getPhoneNum()));
        vh.tv_phoneNum.setText(user.getPhoneNum());
        vh.tv_lastTime.setText(Config.date2zh(user.getLastTime().toString()));
        vh.btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+user.getPhoneNum()));
                mContext.startActivity(i);
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView tv_userName;
        TextView tv_phoneNum;
        TextView tv_lastTime;
        Button btn_call;
    }
}
