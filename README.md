# FluentRecyclerView
功能齐全且易于使用的RecyclerView及BaseAdapter 封装

FluentRecyclerView的使用方式

`<hello world>`
// 启动刷新动态, 触发下拉刷新监听
recyclerView.setRefreshing();
// 关闭/开启 加载动画
recyclerView.setRefreshing(false);
// 设置数据, 无数据时展示空数据界面, 无数据且页码不为1时最下端显示到达底部标识
recyclerView.setData(null, 1);
// 设置空数据界面的提示文字
recyclerView.setCustomDisplayType(0);
// 设置空数据界面的提示文字和图标
recyclerView.setCustomDisplayType(0,0);
// 是否加载更多 默认加载更多
recyclerView.enableLoadMore(true);
// 是否启动下拉刷新功能 默认启动
recyclerView.enablePullToRefresh(true);
// 是否翻转recyclerView,到达顶部加载, 默认false
recyclerView.setTopEnabled(false);

//设置, 下拉刷新 page 为1, 触底加载 page 为实际页码数
recyclerView.setOnRefreshListener(new FluentRefreshListenerImpl(){
           @Override
           public void onLoadMoreRefresh(int page) {
               
           }
       });
```ruby

监听接口
`<hello world>`
public interface FluentRefreshListener {
    /**
     * 下拉刷新
     */
    void onRefreshListener();

    /**
     * 加载更多
     * @param page 页码
     */
    void onLoadMoreRefresh(int page);
}
```ruby
