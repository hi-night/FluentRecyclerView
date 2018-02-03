package com.simple.fluent.recyclerView;

/**
 * FluentRecycler 网络请求监听 适配器
 * Created by duCong on 2/3/2018.
 */
public class FluentRefreshListenerImpl implements FluentRefreshListener {
    @Override
    public void onRefreshListener() {
        onLoadMoreRefresh(FluentRecyclerView.HOME_PAGE);
    }

    @Override
    public void onLoadMoreRefresh(int page) {
    }
}