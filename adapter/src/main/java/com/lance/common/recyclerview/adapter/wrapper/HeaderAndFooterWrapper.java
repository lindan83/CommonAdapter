package com.lance.common.recyclerview.adapter.wrapper;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lance.common.recyclerview.adapter.base.CommonRecyclerViewHolder;
import com.lance.common.recyclerview.adapter.utils.WrapperUtils;

/**
 * 支持HeaderView和FooterView
 */
public class HeaderAndFooterWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int BASE_ITEM_TYPE_HEADER = 100000;
    public static final int BASE_ITEM_TYPE_FOOTER = 200000;

    private SparseArrayCompat<View> headerViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> footerViews = new SparseArrayCompat<>();

    private RecyclerView.Adapter innerAdapter;

    public HeaderAndFooterWrapper(RecyclerView.Adapter adapter) {
        innerAdapter = adapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View headerView = headerViews.get(viewType);
        if (headerView != null) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            headerView.setLayoutParams(lp);
            return CommonRecyclerViewHolder.createViewHolder(parent.getContext(), headerView);
        }
        View footerView = footerViews.get(viewType);
        if (footerView != null) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            footerView.setLayoutParams(lp);
            return CommonRecyclerViewHolder.createViewHolder(parent.getContext(), footerView);
        }
        return innerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)) {
            return headerViews.keyAt(position);
        } else if (isFooterViewPos(position)) {
            return footerViews.keyAt(position - getHeadersCount() - getRealItemCount());
        }
        return innerAdapter.getItemViewType(position - getHeadersCount());
    }

    //获取真实数据项数量
    private int getRealItemCount() {
        return innerAdapter.getItemCount();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            return;
        }
        innerAdapter.onBindViewHolder(holder, position - getHeadersCount());
    }

    @Override
    public int getItemCount() {
        return getHeadersCount() + getFootersCount() + getRealItemCount();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        WrapperUtils.onAttachedToRecyclerView(innerAdapter, recyclerView, new WrapperUtils.SpanSizeCallback() {
            @Override
            public int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position) {
                int viewType = getItemViewType(position);
                if (headerViews.get(viewType) != null) {
                    return layoutManager.getSpanCount();
                } else if (footerViews.get(viewType) != null) {
                    return layoutManager.getSpanCount();
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
        int position = holder.getLayoutPosition();
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            WrapperUtils.setFullSpan(holder);
        }
    }

    //判断指定position是否HeaderView的位置
    private boolean isHeaderViewPos(int position) {
        return position < getHeadersCount();
    }

    //判断指定position是否FooterView的位置
    private boolean isFooterViewPos(int position) {
        return position >= getHeadersCount() + getRealItemCount();
    }

    /**
     * 添加自定义HeaderView
     */
    public HeaderAndFooterWrapper addHeaderView(View view) {
        headerViews.put(headerViews.size() + BASE_ITEM_TYPE_HEADER, view);
        return this;
    }

    /**
     * 添加自定义FooterView
     */
    public HeaderAndFooterWrapper addFooterView(View view) {
        footerViews.put(footerViews.size() + BASE_ITEM_TYPE_FOOTER, view);
        return this;
    }

    /**
     * 获取HeaderView数量
     */
    public int getHeadersCount() {
        return headerViews.size();
    }

    /**
     * 获取FooterView数量
     */
    public int getFootersCount() {
        return footerViews.size();
    }
}