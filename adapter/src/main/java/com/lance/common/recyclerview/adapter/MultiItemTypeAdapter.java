package com.lance.common.recyclerview.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.lance.common.recyclerview.adapter.base.CommonRecyclerViewHolder;
import com.lance.common.recyclerview.adapter.base.ItemViewDelegate;
import com.lance.common.recyclerview.adapter.base.ItemViewDelegateManager;

import java.util.List;

/**
 * Created by lindan on 16-11-2.
 * 支持多item type的RecyclerView适配器
 */

public class MultiItemTypeAdapter<T> extends AbstractRecyclerViewAdapter<T, CommonRecyclerViewHolder> {
    protected ItemViewDelegateManager itemViewDelegateManager;


    public MultiItemTypeAdapter(Context context, List<T> data) {
        super(context, data);
        this.itemViewDelegateManager = new ItemViewDelegateManager();
    }

    @Override
    public int getItemViewType(int position) {
        if (!useItemViewDelegateManager()) {
            return super.getItemViewType(position);
        }
        return itemViewDelegateManager.getItemViewType(data.get(position), position);
    }


    @Override
    public CommonRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewDelegate itemViewDelegate = itemViewDelegateManager.getItemViewDelegate(viewType);
        int layoutId = itemViewDelegate.getItemViewLayoutId();
        CommonRecyclerViewHolder holder = CommonRecyclerViewHolder.createViewHolder(context, parent, layoutId);
        onViewHolderCreated(holder, holder.getConvertView());
        setListener(parent, holder, viewType);
        return holder;
    }

    public void onViewHolderCreated(CommonRecyclerViewHolder holder, View itemView) {

    }

    public void convert(CommonRecyclerViewHolder holder, T t) {
        itemViewDelegateManager.convert(holder, t, holder.getAdapterPosition());
    }

    protected boolean isEnabled(int viewType) {
        return true;
    }

    protected void setListener(final ViewGroup parent, final CommonRecyclerViewHolder viewHolder, int viewType) {
        if (!isEnabled(viewType)) return;
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    onItemClickListener.onItemClick(v, viewHolder, position);
                }
            }
        });

        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    return onItemClickListener.onItemLongClick(v, viewHolder, position);
                }
                return false;
            }
        });
    }

    @Override
    public void onBindViewHolder(CommonRecyclerViewHolder holder, int position) {
        convert(holder, data.get(position));
    }

    public MultiItemTypeAdapter addItemViewDelegate(ItemViewDelegate<T> itemViewDelegate) {
        itemViewDelegateManager.addDelegate(itemViewDelegate);
        return this;
    }

    public MultiItemTypeAdapter addItemViewDelegate(int viewType, ItemViewDelegate<T> itemViewDelegate) {
        itemViewDelegateManager.addDelegate(viewType, itemViewDelegate);
        return this;
    }

    protected boolean useItemViewDelegateManager() {
        return itemViewDelegateManager.getItemViewDelegateCount() > 0;
    }
}