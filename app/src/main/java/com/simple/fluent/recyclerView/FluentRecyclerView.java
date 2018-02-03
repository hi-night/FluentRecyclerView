package com.simple.fluent.recyclerView;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.simple.fluent.R;

import java.util.List;


/**
 * Created by duCong on 2/3/2018.
 *
 * @param <T> item 数据类型
 */
public class FluentRecyclerView<T> extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    // 用于控制加载更加,在 下拉刷新 或 加载更多时不重复进入
    private int currentState = STATE_IDLE;

    // 空闲状态
    public static final int STATE_IDLE = 0;
    // 下拉刷新
    public static final int STATE_REFRESH = 1;
    // 加载更多
    public static final int STATE_MORE_REFRESH = 2;

    // 首页
    public static final int HOME_PAGE = 1;

    // 是否加载更多
    private boolean isLoadMoreEnabled = true;
    // 是否下拉刷新
    private boolean isRefreshEnabled = true;
    // 用户设置是否加载更多
    private boolean isFirstLoadMoreEnabled = true;
    // 是否显示底部
    private boolean isShowEnding = true;
    //是否翻转,用于判断是否到底部
    private boolean isTopEnabled;

    private FluentRefreshListener refreshListener;

    private FluentBaseAdapter adapter;
    private int page = STATE_REFRESH;
    public final int[] COLORS = {0xff2196F3};

    // 空面面
    private TextView emptyView;


    public FluentRecyclerView(Context context) {
        super(context);
        setUpView();
    }

    public FluentRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpView();
    }

    public FluentRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUpView();
    }

    private void setUpView() {
        LayoutInflater.from(getContext()).inflate(R.layout.fluent_view, this, true);

        setSwipeRefreshLayout();

        setRecyclerView();
    }

    private void setSwipeRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(COLORS);
        swipeRefreshLayout.setOnRefreshListener(this);
        emptyView = (TextView) findViewById(R.id.empty_view);
    }

    private void setRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                        (!recyclerView.canScrollVertically(1) || isTopEnabled && !recyclerView.canScrollVertically(-1))) {

                    if (isLoadingData()) {
                        currentState = STATE_MORE_REFRESH;

                        if (adapter.getDataCount() < 10 || !isFirstLoadMoreEnabled) {
                            inEnableLoadMore(false);
                            adapter.onEndingStateChanged(isShowEnding);
                        } else {
                            refreshListener.onLoadMoreRefresh(++page);
                        }
                    }
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        //去掉Item局部刷新的动画
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }


    /**
     * 加载状态为空闲,允许加载更多,加载更多监听不为空时为True
     */
    private boolean isLoadingData() {
        return currentState == STATE_IDLE && isLoadMoreEnabled && refreshListener != null && adapter.getData() != null;
    }


    /**
     * 是否加载更多
     *
     * @param enable
     */
    public void enableLoadMore(boolean enable) {
        isFirstLoadMoreEnabled = enable;
        isLoadMoreEnabled = enable;
    }

    private void inEnableLoadMore(boolean enable) {
        isLoadMoreEnabled = enable;
    }

    /**
     * 是否显示已到最底部
     *
     * @param showEnding
     */
    public void setShowEnding(boolean showEnding) {
        isShowEnding = showEnding;
    }

    /**
     * 是否下拉刷新
     *
     * @param enable
     */
    public void enablePullToRefresh(boolean enable) {
        isRefreshEnabled = enable;
        swipeRefreshLayout.setEnabled(enable);
    }

    /**
     * 是否到达项部加载
     */
    public void setTopEnabled(boolean isTopEnabled) {
        this.isTopEnabled = isTopEnabled;
    }

    /**
     * 强制显示无数据页面
     */
    public void setForceEmptyView(boolean isVisibity) {
        recyclerView.setVisibility(isVisibity ? GONE : VISIBLE);
    }

    public void setCustomDisplayType(int message, int icon) {
        emptyView.setText(getContext().getString(message));
        emptyView.setCompoundDrawablesWithIntrinsicBounds(null, getContext().getResources().getDrawable(icon), null, null);
    }

    public void setCustomDisplayType(int message) {
        emptyView.setText(getContext().getString(message));
    }

    /**
     * 设置Layout
     *
     * @param manager
     */
    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        recyclerView.setLayoutManager(manager);
    }

    public void setAdapter(FluentBaseAdapter adapter) {
        this.adapter = adapter;
        recyclerView.setAdapter(adapter);
    }

    public void setNewData(List<T> data) {
        setData(data, STATE_REFRESH);
    }

    public void setAllData(List<T> data) {
        setData(data, STATE_MORE_REFRESH);
    }

    public void setData(List<T> data, int page) {
        onRefreshCompleted();
        if (adapter == null) return;

        // 重置底部标识
        adapter.onEndingStateChanged(false);

        if (page == STATE_REFRESH) {

            if ((data == null || data.size() == 0) && adapter.isNoHeaderView()) {
                recyclerView.setVisibility(GONE);
            } else {
                recyclerView.setVisibility(VISIBLE);
            }

            adapter.setNewData(data);
        } else {
            adapter.addAllData(data);
        }


        if (data == null || data.size() < 10 || !isFirstLoadMoreEnabled) {
            inEnableLoadMore(false);
            adapter.onEndingStateChanged(isShowEnding);
        } else {
            inEnableLoadMore(true);
        }
    }

    /**
     * 刷新动作
     */
    public void setRefreshing() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }

    public void setRefreshing(boolean isVisibity) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(isVisibity);
    }

    public void setOnRefreshListener(FluentRefreshListener listener) {
        this.refreshListener = listener;
    }

    public FluentBaseAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onRefresh() {
        currentState = STATE_REFRESH;
        page = STATE_REFRESH;
        if (refreshListener != null) {
            refreshListener.onRefreshListener();
            inEnableLoadMore(isFirstLoadMoreEnabled);
        }
    }

    /**
     * 关闭下拉刷新 或 加载更多的动画
     */
    public void onRefreshCompleted() {
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(isRefreshEnabled);
        currentState = STATE_IDLE;
    }

    /**
     * 滚动到指定Items
     *
     * @param position
     */
    public void setSelection(int position) {
        recyclerView.scrollToPosition(position);
    }
}
