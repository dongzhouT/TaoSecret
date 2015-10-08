package secret.com.tao.taosecret.aty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import secret.com.tao.taosecret.R;
import secret.com.tao.taosecret.net.Comment;

/**
 * Created by Adminstrator on 2015/9/22.
 */
public class AtyMessageListAdapter extends BaseAdapter {
    private Context mContext;
    private List<Comment> comments = new ArrayList<Comment>();

    public AtyMessageListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void addAll(List<Comment> data) {
        this.comments.addAll(data);
        notifyDataSetChanged();
    }

    public void clear() {
        this.comments.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Comment getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.aty_comment_list_cell, null);
            vh.tv = (TextView) convertView.findViewById(R.id.tv_comment_cell);
            vh.tv_time = (TextView) convertView.findViewById(R.id.tv_comment_time_cell);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.tv.setText(getItem(position).getContent());
        vh.tv_time.setText(getItem(position).getCmttime());
        return convertView;
    }

    private class ViewHolder {
        public TextView tv;
        public TextView tv_time;
    }
}
