package com.lance.common.recyclerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import com.lance.common.recyclerview.adapter.base.CommonRecyclerViewHolder;
import com.lance.common.recyclerview.adapter.base.ItemViewDelegate;

import java.util.List;

/**
 * Created by lindan on 16-11-2.
 * 用于RecyclerView的通用Adapter
 */

public abstract class CommonRecyclerViewAdapter<T> extends MultiItemTypeAdapter<T> {
    protected int layoutId;
    protected LayoutInflater inflater;

    public CommonRecyclerViewAdapter(final Context context, final int layoutId, List<T> data) {
        super(context, data);
        this.inflater = LayoutInflater.from(context);
        this.layoutId = layoutId;

        addItemViewDelegate(new ItemViewDelegate<T>() {
            @Override
            public int getItemViewLayoutId() {
                return layoutId;
            }

            @Override
            public boolean isForViewType(T item, int position) {
                return true;
            }

            @Override
            public void convert(CommonRecyclerViewHolder holder, T item, int position) {
                CommonRecyclerViewAdapter.this.convert(holder, item, position);
            }
        });
    }

    protected abstract void convert(CommonRecyclerViewHolder holder, T item, int position);
}