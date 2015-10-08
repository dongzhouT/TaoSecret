package secret.com.tao.taosecret.aty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import secret.com.tao.taosecret.R;
import secret.com.tao.taosecret.net.Message;

/**
 * Created by Adminstrator on 2015/9/22.
 */
public class AtyTimeLineMessageListAdapter extends BaseAdapter {
    private Context mContext;
    private List<Message> messages=new ArrayList<Message>();

    public AtyTimeLineMessageListAdapter(Context mContext) {
        this.mContext = mContext;
    }
    public void addAll(List<Message> data){
        messages.addAll(data);
        notifyDataSetChanged();
    }
    public void clear(){
        messages.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=new ViewHolder();
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.aty_timeline_list_cell,null);
            viewHolder.view= (TextView) convertView.findViewById(R.id.tv_msg_cell);
            viewHolder.time= (TextView) convertView.findViewById(R.id.tv_time_cell);
            viewHolder.cmtCount= (TextView) convertView.findViewById(R.id.tv_msg_comment_count);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.view.setText(messages.get(position).getMsg());
        viewHolder.time.setText(messages.get(position).getMsgtime());
        viewHolder.cmtCount.setText(messages.get(position).getCommentCount()+"");

        return convertView;
    }
    private class ViewHolder{
        public TextView view;
        public TextView time;
        public TextView cmtCount;
    }
}
