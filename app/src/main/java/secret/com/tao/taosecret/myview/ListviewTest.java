package secret.com.tao.taosecret.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import secret.com.tao.taosecret.R;

/**
 * Created by Adminstrator on 2015/9/28.
 */
public class ListviewTest extends ListView {
    private Context mContext;
    private OnLoadListener onLoadListener;
    boolean finish;
    View foot_listview;

    public ListviewTest(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        removeFooterView(foot_listview);
    }

    public ListviewTest(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public ListviewTest(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    public void init() {
        setOnScrollListener(new MyScrollListener());
        foot_listview = View.inflate(mContext, R.layout.foot_listview, null);
        addFooterView(foot_listview);
        setFastScrollEnabled(true);
        finish=true;
    }

    class MyScrollListener implements OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            Log.v("listao", "state:" + scrollState);
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int currentCount = firstVisibleItem + visibleItemCount;
            if (currentCount == totalItemCount && finish) {//滑动到最后一条
                if (onLoadListener != null) {
                    finish = false;
                    addFooterView(foot_listview);
                    onLoadListener.onload();
                }
            }
        }
    }

    public interface OnLoadListener {
        void onload();
    }


    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    public void OnFinish() {
        finish = true;
        if (getFooterViewsCount() > 0) {
            removeFooterView(foot_listview);
        }
    }
}
