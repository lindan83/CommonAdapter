package com.lance.common.recyclerview.adapter.wrapper;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lance.common.recyclerview.adapter.base.CommonRecyclerViewHolder;
import com.lance.common.recyclerview.adapter.utils.WrapperUtils;

/**
 * 空视图
 */
public class EmptyWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ITEM_TYPE_EMPTY = Integer.MAX_VALUE - 1;

    private RecyclerView.Adapter innerAdapter;
    private View emptyView;
    private int emptyViewLayoutId;

    public EmptyWrapper(RecyclerView.Adapter adapter) {
        innerAdapter = adapter;
    }

    private boolean isEmpty() {
        return (emptyView != null || emptyViewLayoutId != 0) && innerAdapter.getItemCount() == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isEmpty()) {
            CommonRecyclerViewHolder holder;
            if (emptyView != null) {
                holder = CommonRecyclerViewHolder.createViewHolder(parent.getContext(), emptyView);
            } else {
                holder = CommonRecyclerViewHolder.createViewHolder(parent.getContext(), parent, emptyViewLayoutId);
            }
            return holder;
        }
        return innerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        WrapperUtils.onAttachedToRecyclerView(innerAdapter, recyclerView, new WrapperUtils.SpanSizeCallback() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position) {
                if (isEmpty()) {
                    return gridLayoutManager.getSpanCount();
                }
                if (oldLookup != null) {
                    return oldLookup.getSpanSize(position);
                }
                return 1;
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        innerAdapter.onViewAttachedToWindow(holder);
        if (isEmpty()) {
            WrapperUtils.setFullSpan(holder);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (isEmpty()) {
            return ITEM_TYPE_EMPTY;
        }
        return innerAdapter.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isEmpty()) {
            return;
        }
        innerAdapter.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        if (isEmpty()) {
            return 1;
        }
        return innerAdapter.getItemCount();
    }

    /**
     * 设置自定义EmptyView
     */
    public EmptyWrapper setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        return this;
    }

    /**
     * 设置自定义EmptyView
     */
    public EmptyWrapper setEmptyView(int layoutId) {
        emptyViewLayoutId = layoutId;
        return this;
    }
}