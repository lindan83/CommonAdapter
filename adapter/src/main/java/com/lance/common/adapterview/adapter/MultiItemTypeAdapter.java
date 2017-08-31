package com.lance.common.adapterview.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lance.common.adapterview.adapter.base.ItemViewDelegate;
import com.lance.common.adapterview.adapter.base.ItemViewDelegateManager;

import java.util.List;

public class MultiItemTypeAdapter<T> extends BaseAdapter {
    protected Context context;
    protected List<T> data;
    private ItemViewDelegateManager itemViewDelegateManager;

    public MultiItemTypeAdapter(Context context, List<T> data) {
        this.context = context;
        this.data = data;
        itemViewDelegateManager = new ItemViewDelegateManager();
    }

    public MultiItemTypeAdapter addItemViewDelegate(ItemViewDelegate<T> itemViewDelegate) {
        itemViewDelegateManager.addDelegate(itemViewDelegate);
        return this;
    }

    private boolean useItemViewDelegateManager() {
        return itemViewDelegateManager.getItemViewDelegateCount() > 0;
    }

    @Override
    public int getViewTypeCount() {
        if (useItemViewDelegateManager()) {
            return itemViewDelegateManager.getItemViewDelegateCount();
        }
        return super.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (useItemViewDelegateManager()) {
            return itemViewDelegateManager.getItemViewType(data.get(position), position);
        }
        return super.getItemViewType(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int layoutId = itemViewDelegateManager.getItemViewLayoutId(data.get(position), position);
        ViewHolder viewHolder = ViewHolder.get(context, convertView, parent, layoutId, position);
        convert(viewHolder, getItem(position), position);
        return viewHolder.getConvertView();
    }

    protected void convert(ViewHolder viewHolder, T item, int position) {
        itemViewDelegateManager.convert(viewHolder, item, position);
    }

    @Override
    public int getCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return data != null && position >= 0 && position < data.size() ? data.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}