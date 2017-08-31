package com.lance.common.recyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.lance.common.recyclerview.adapter.base.CommonRecyclerViewHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * RecyclerView adapter designed to wrap an existing adapter allowing the addition of
 * header views and footer views.
 * I implemented it to aid with the transition from ListView to RecyclerView where the ListView's
 * addHeaderView and addFooterView methods were used. Using this class you may initialize your
 * header views in the Fragment/Activity and add them to the adapter in the same way you used to
 * add them to a ListView.
 * I also required to be able to swap out multiple adapters with different content, therefore
 * setAdapter may be called multiple times.
 * Created by darnmason on 07/11/2014.
 * Modified by lindan on 08/31/2017
 */

public class PullLoadAndRefreshRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int HEADERS_START = Integer.MIN_VALUE;
    public static final int FOOTERS_START = Integer.MIN_VALUE + 10000;
    public static final int ITEMS_START = Integer.MIN_VALUE + 20000;
    public static final int ADAPTER_MAX_TYPES = 100;

    private RecyclerView.Adapter innerAdapter;
    private SparseArray<View> headerViews;
    private SparseArray<View> footerViews;
    private Map<Class, Integer> itemTypesOffset;

    private RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            notifyItemRangeChanged(positionStart + getHeaderCount(), itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            notifyItemRangeInserted(positionStart + getHeaderCount(), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            notifyItemRangeRemoved(positionStart + getHeaderCount(), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            int headerCount = getHeaderCount();
            notifyItemRangeChanged(fromPosition + headerCount, toPosition + headerCount + itemCount);
        }
    };

    /**
     * Construct a new header view recycler adapter
     *
     * @param adapter The underlying adapter to wrap
     */
    public PullLoadAndRefreshRecyclerViewAdapter(RecyclerView.Adapter adapter) {
        headerViews = new SparseArray<>();
        footerViews = new SparseArray<>();
        itemTypesOffset = new HashMap<>();
        setInnerAdapter(adapter);
    }

    /**
     * Replaces the underlying adapter, notifying RecyclerView of changes
     *
     * @param adapter The new adapter to wrap
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (innerAdapter != null && innerAdapter.getItemCount() > 0) {
            notifyItemRangeRemoved(getHeaderCount(), innerAdapter.getItemCount());
        }
        setInnerAdapter(adapter);
        notifyItemRangeInserted(getHeaderCount(), innerAdapter.getItemCount());
    }

    @Override
    public int getItemViewType(int position) {
        int headerCount = getHeaderCount();
        if (position < headerCount) {
            return HEADERS_START + position;
        }
        int itemCount = innerAdapter.getItemCount();
        if (position < headerCount + itemCount) {
            return getAdapterTypeOffset() + innerAdapter.getItemViewType(position - headerCount);
        } else {
            return FOOTERS_START + position - headerCount - itemCount;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType < HEADERS_START + getHeaderCount()) {
            return CommonRecyclerViewHolder.createViewHolder(viewGroup.getContext(), headerViews.get(viewType - HEADERS_START));
        }
        if (viewType < FOOTERS_START + getFooterCount()) {
            return CommonRecyclerViewHolder.createViewHolder(viewGroup.getContext(), footerViews.get(viewType - FOOTERS_START));
        }
        return innerAdapter.onCreateViewHolder(viewGroup, viewType - getAdapterTypeOffset());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int headerCount = getHeaderCount();
        int itemCount = innerAdapter.getItemCount();
        if (position < headerCount) {
            ViewGroup.LayoutParams lp = viewHolder.itemView.getLayoutParams();
            RecyclerView recyclerView = (RecyclerView) viewHolder.itemView.getParent();
            lp.width = recyclerView.getWidth() - recyclerView.getPaddingLeft() - recyclerView.getPaddingRight();
            viewHolder.itemView.setLayoutParams(lp);
        } else if (position < headerCount + itemCount) {
            innerAdapter.onBindViewHolder(viewHolder, position - headerCount);
        } else {
            ViewGroup.LayoutParams lp = viewHolder.itemView.getLayoutParams();
            RecyclerView recyclerView = (RecyclerView) viewHolder.itemView.getParent();
            lp.width = recyclerView.getWidth() - recyclerView.getPaddingLeft() - recyclerView.getPaddingRight();
            viewHolder.itemView.setLayoutParams(lp);
        }
    }

    /**
     * Add a static view to appear at the start of the RecyclerView. Headers are displayed in the
     * order they were added.
     *
     * @param view The header view to add
     */
    public PullLoadAndRefreshRecyclerViewAdapter addHeaderView(View view) {
        headerViews.put(headerViews.size(), view);
        return this;
    }

    /**
     * Add a static view to appear at the end of the RecyclerView. Footers are displayed in the
     * order they were added.
     *
     * @param view The footer view to add
     */
    public PullLoadAndRefreshRecyclerViewAdapter addFooterView(View view) {
        footerViews.put(footerViews.size(), view);
        return this;
    }

    @Override
    public int getItemCount() {
        return getHeaderCount() + getFooterCount() + getWrappedItemCount();
    }

    /**
     * @return The item count in the underlying adapter
     */
    public int getWrappedItemCount() {
        return innerAdapter.getItemCount();
    }

    /**
     * @return The number of header views added
     */
    public int getHeaderCount() {
        return headerViews.size();
    }

    /**
     * @return The number of footer views added
     */
    public int getFooterCount() {
        return footerViews.size();
    }

    private PullLoadAndRefreshRecyclerViewAdapter setInnerAdapter(RecyclerView.Adapter adapter) {
        if (innerAdapter != null) {
            innerAdapter.unregisterAdapterDataObserver(dataObserver);
        }
        innerAdapter = adapter;
        Class adapterClass = innerAdapter.getClass();
        if (!itemTypesOffset.containsKey(adapterClass)) {
            putAdapterTypeOffset(adapterClass);
        }
        innerAdapter.registerAdapterDataObserver(dataObserver);
        return this;
    }

    public RecyclerView.Adapter getInnerAdapter() {
        return innerAdapter;
    }

    private void putAdapterTypeOffset(Class adapterClass) {
        itemTypesOffset.put(adapterClass, ITEMS_START + itemTypesOffset.size() * ADAPTER_MAX_TYPES);
    }

    private int getAdapterTypeOffset() {
        return itemTypesOffset.get(innerAdapter.getClass());
    }
}