package com.simple.fluent.recyclerView;

/**
 * FluentRecycler 网络请求监听 适配器
 * Created by duCong on 2/3/2018.
 */
public interface FluentRefreshListener {
    //下拉刷新
    void onRefreshListener();

    //加载更多
    void onLoadMoreRefresh(int page);
}