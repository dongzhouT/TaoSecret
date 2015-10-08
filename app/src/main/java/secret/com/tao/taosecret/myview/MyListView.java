package secret.com.tao.taosecret.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import secret.com.tao.taosecret.R;

/**
 * Created by Adminstrator on 2015/8/19.
 * 下拉刷新控件
 */
public class MyListView extends ListView implements AbsListView.OnScrollListener {
    OnRefreshListener mOnRefreshListener;
    private int firstVisibleIndex;
    private View headerView;
    private ImageView arrow;
    private TextView title;
    private TextView lastUpdate;
    private String TAG = "LISTVIEW";
    private int headerContentWidth;
    private int headerContentHeight;
    private Animation animation;
    private Animation reverseAnimation;
    private static final int PULL_TO_REFRESH = 0;
    private static final int RELEASE_TO_REFRESH = 1;
    private static final int REFRESHING = 2;
    private static final int DONE = 3;
    private int state;//当前下拉刷新控件的状态
    private boolean isRefreshable;
    private float startY;
    private boolean isRecored;
    private ProgressBar progressBar;
    private boolean isBack;
    private final int RATIO = 3;
    //以下是加载更多功能需要的属性
    private boolean finish = true;//滑动结束
    private OnLoadListener onLoadListener;//加载工作的监听器
    View footer;
    int maxPage = 0;


    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    DateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

    public MyListView(Context context) {
        super(context);
        init(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        headerView = inflater.inflate(R.layout.header, null);
        arrow = (ImageView) headerView.findViewById(R.id.arrow);
        progressBar = (ProgressBar) headerView.findViewById(R.id.progressBar);
        title = (TextView) headerView.findViewById(R.id.title);
        lastUpdate = (TextView) headerView.findViewById(R.id.last_update);
        arrow.setMaxWidth(30);
        arrow.setMaxHeight(50);
        measureView(headerView);
        headerContentWidth = headerView.getMeasuredWidth();
        //todo
        headerContentHeight = headerView.getMeasuredHeight();//getMeasuredHeight和getHeight的区别是什么？？？?
        headerView.setPadding(0, -1 * headerContentHeight, 0, 0);
        headerView.invalidate();//headView 重绘
        addHeaderView(headerView);

        animation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(true);
        animation.setDuration(100);
        animation.setInterpolator(new LinearInterpolator());
        reverseAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setFillAfter(true);
        reverseAnimation.setDuration(100);
        reverseAnimation.setInterpolator(new LinearInterpolator());

        setOnScrollListener(this);
        state = GONE;
        isRefreshable = false;

        //加载更多
        footer = View.inflate(context, R.layout.foot_listview, null);

    }

    private void measureView(View child) {
        ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childMeasureWidth = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
        int childMeasureHeight;
        if (lp.height > 0) {
            childMeasureHeight = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
        } else {
            childMeasureHeight = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childMeasureWidth, childMeasureHeight);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        firstVisibleIndex = firstVisibleItem;
        int currentCount = firstVisibleItem + visibleItemCount;
        if (currentCount == totalItemCount && finish && maxPage == 0) {//滑动到最后一条
            if (onLoadListener != null) {
                finish = false;
                addFooterView(footer);
                onLoadListener.onLoad();
            }
        }
    }


    public interface OnRefreshListener {
        void onRefresh();
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefreshListener = listener;
        isRefreshable = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isRefreshable) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (firstVisibleIndex == 0 && !isRecored) {
                        startY = ev.getY();
                        isRecored = true;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    float tempY = ev.getY();
                    if (firstVisibleIndex == 0 && !isRecored) {
                        startY = ev.getY();
                        isRecored = true;
                    }
                    if (state != REFRESHING) {
                        if (state == PULL_TO_REFRESH) {
                            if ((tempY - startY) / RATIO > headerContentHeight) {
                                state = RELEASE_TO_REFRESH;
                                changeHeadViewOfState();
                            } else if (tempY - startY <= 0) {
                                state = GONE;
                                changeHeadViewOfState();
                            }
                        }
                        if (state == RELEASE_TO_REFRESH) {
                            if ((tempY - startY) / RATIO < headerContentHeight && (tempY - startY) > 0) {
                                state = PULL_TO_REFRESH;
                                isBack = true;
                                changeHeadViewOfState();
                            } else if (tempY - startY <= 0) {
                                state = GONE;
                                changeHeadViewOfState();
                            }
                        }
                        if (state == GONE) {
                            if (tempY - startY > 0) {
                                state = PULL_TO_REFRESH;
                                changeHeadViewOfState();
                            }
                        }
                        if (state == RELEASE_TO_REFRESH || state == PULL_TO_REFRESH) {
                            headerView.setPadding(0, (int) ((tempY - startY) / RATIO - headerContentHeight), 0, 0);
                        }
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    isRecored = false;
                    if (state != REFRESHING) {
                        if (state == GONE) {
                            //do nothing
                        }
                        if (state == PULL_TO_REFRESH) {
                            state = GONE;
                            changeHeadViewOfState();
                        }
                        if (state == RELEASE_TO_REFRESH) {
                            state = REFRESHING;
                            changeHeadViewOfState();
                            onRefresh();
                        }
                    }
                    break;

            }
        }
        return super.onTouchEvent(ev);
    }

    private void onRefresh() {
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
        }
    }

    private void changeHeadViewOfState() {
        switch (state) {
            case PULL_TO_REFRESH:
                arrow.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                title.setVisibility(View.VISIBLE);
                lastUpdate.setVisibility(View.VISIBLE);
                arrow.clearAnimation();
                title.setText("下拉");
                if (isBack) {
                    arrow.startAnimation(animation);
                    isBack = false;
                }
                break;
            case RELEASE_TO_REFRESH:
                arrow.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                title.setVisibility(View.VISIBLE);
                lastUpdate.setVisibility(View.VISIBLE);
                arrow.clearAnimation();
                arrow.startAnimation(reverseAnimation);
                title.setText("释放刷新");
                break;
            case REFRESHING:
                arrow.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                title.setVisibility(View.VISIBLE);
                lastUpdate.setVisibility(View.VISIBLE);
                arrow.clearAnimation();
                title.setText("加载中...");
                headerView.setPadding(0, 0, 0, 0);
                break;
            case GONE:
                arrow.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                title.setVisibility(View.GONE);
                lastUpdate.setVisibility(View.GONE);
                title.setText("下拉");
                arrow.clearAnimation();
                headerView.setPadding(0, -1 * headerContentHeight, 0, 0);
                break;
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        lastUpdate.setText("最近更新时间:" + df.format(new Date()));
        addFooterView(footer);
        super.setAdapter(adapter);
        removeFooterView(footer);
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    public interface OnLoadListener {
        void onLoad();
    }

    //刷新结束后，需要调用该方法
    public void onRefreshComplete() {
        state = GONE;
        isRecored = false;
        changeHeadViewOfState();

        lastUpdate.setText("最近更新时间:" + df.format(new Date()));

    }

    //加载完成后，需要调用该方法，隐藏footer
    public void onLoadComplete() {
        Log.v("listao", "onLoadComplete~~~~~~~~~~~~~");
        finish = true;
        if (getFooterViewsCount() > 0) {
            removeFooterView(footer);
        }
    }
}
