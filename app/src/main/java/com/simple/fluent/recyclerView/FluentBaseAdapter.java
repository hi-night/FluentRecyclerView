package com.simple.fluent.recyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simple.fluent.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by duCong on 2/3/2018.
 * @param <T> item 数据类型
 */
public abstract class FluentBaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 头部
    protected static final int TYPE_MORE_HEADER = 100003;
    // 加载中
    protected static final int TYPE_MORE_FOOTER = 100002;
    // 已到底部
    protected static final int TYPE_MORE_END = 100004;

    protected Context context;
    protected ArrayList<T> dataList;


    // 是否显示加载更多
    protected boolean isMoreFooter = true;
    // 是否显示已到底部
    protected boolean isMoreEnding;

    // 头部
    private View headerView;

    protected int position;


    public FluentBaseAdapter(Context context, List<T> mDataList) {
        this.dataList = mDataList == null ? new ArrayList<T>() : new ArrayList<>(mDataList);
        this.context = context;

    }

    /**
     * 追加Item
     */
    public void add(T item) {
        boolean isAdd = dataList.add(item);
        if (isAdd)
            notifyItemInserted(dataList.size() + getHeaderViewsCount());
    }

    /**
     * 添加指定位置的Item
     */
    public void add(int position, T item) {
        if (position < 0 || position > dataList.size()) {
            return;
        }
        boolean isAdd = dataList.add(item);
        if (isAdd)
            notifyItemInserted(position + getHeaderViewsCount());
    }

    /**
     * 删除数据
     */
    public void remove(T item) {
        int index = dataList.indexOf(item);
        boolean isRemoved = dataList.remove(item);
        if (isRemoved)
            notifyItemRemoved(index + getHeaderViewsCount());
    }

    /**
     * 通过位置删除数据
     */
    public void remove(int position) {
        if (position < 0 || position >= dataList.size()) {
            return;
        }
        dataList.remove(position);
        notifyItemRemoved(position + getHeaderViewsCount());
    }

    public int getHeaderViewsCount() {
        return headerView == null ? 0 : 1;
    }

    /**
     * 追加多条Items
     */
    public void addAllData(List<T> data) {
        if (data != null && data.size() > 0) {
            this.dataList.addAll(data);
        }

        notifyDataSetChanged();
    }

    public void upData(T data, int position) {
        dataList.set(position, data);

        if (headerView == null) {
            notifyItemChanged(position);
        } else {
            notifyItemChanged(position + 1);
        }

    }

    /**
     * 重新载入新的数据
     */
    public void setNewData(List<T> data) {
        dataList.clear();
        addAllData(data);
    }


    /**
     * 获取数据
     */
    public ArrayList<T> getData() {
        return dataList;
    }

    /**
     * 添加Header
     */
    public void addHeaderView(View header) {
        if (header == null) return;
        this.headerView = header;
    }

    public boolean isNoHeaderView() {
        return headerView == null;
    }

    /**
     * 获取指定条目数据
     *
     * @param position
     * @return
     */
    public T getItem(int position) {
        if (position < 0 || position >= dataList.size()) {
            return null;
        }
        return dataList.get(position);
    }

    protected int getPosition() {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_MORE_FOOTER) {
            return onCreateLoadMoreFooterViewHolder(parent);
        } else if (viewType == TYPE_MORE_END) {
            return onCreateEndingViewHolder(parent);
        } else if (viewType == TYPE_MORE_HEADER) {
            return new LoadMoreFooterViewHolder(headerView);
        } else {
            return onCreateNormalViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        this.position = getPosition(position);

        int itemViewType = getItemViewType(position);

        if (itemViewType != TYPE_MORE_HEADER
                && itemViewType != TYPE_MORE_END
                && itemViewType != TYPE_MORE_FOOTER) {

            onAdapterBindViewHolder(holder, dataList.get(getPosition(position)));
        }
    }

    protected Context getContext() {
        return context;
    }

    @Override
    public int getItemCount() {
        return getDataCount() + (headerView != null ? 1 : 0);
    }

    public int getDataCount() {
        if (dataList == null || dataList.size() == 0) {
            return 0;
        }else {
            return getFooterAndEndItemCount();
        }
    }

    public int getFooterAndEndItemCount() {
        if (isMoreFooter || isMoreEnding)
            return dataList.size() + 1;

        return dataList.size();
    }


    public int getPosition(int position) {
        return position - (headerView != null ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (headerView != null && position == 0) {
            return TYPE_MORE_HEADER;
        } else if (isMoreEnding && position == getItemCount() - 1) {
            return TYPE_MORE_END;
        } else if (isMoreFooter && position == getItemCount() -1) {
            return TYPE_MORE_FOOTER;
        }

        return getDataViewType(getPosition(position));
    }

    protected int getDataViewType(int position) {
        return 0;
    }

    public void onLoadMoreStateChanged(boolean isShown) {
        isMoreFooter = isShown;
    }

    public void onEndingStateChanged(boolean isShown) {
        isMoreEnding = isShown;
        notifyItemChanged(getItemCount());
    }

    protected abstract void onAdapterBindViewHolder(RecyclerView.ViewHolder holder, T item);

    protected abstract RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType);


    protected RecyclerView.ViewHolder onCreateLoadMoreFooterViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fluent_view_loading, parent, false);
        return new LoadMoreFooterViewHolder(view);
    }

    protected RecyclerView.ViewHolder onCreateEndingViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fluent_view_end, parent, false);
        return new LoadMoreFooterViewHolder(view);
    }

    private class LoadMoreFooterViewHolder extends RecyclerView.ViewHolder {
        public LoadMoreFooterViewHolder(View view) {
            super(view);
        }
    }
}
