package com.lance.common.recyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * RecyclerView adapter designed to wrap an existing adapter allowing the addition of
 * header views and footer views.
 * </p>
 * <p>
 * I implemented it to aid with the transition from ListView to RecyclerView where the ListView's
 * addHeaderView and addFooterView methods were used. Using this class you may initialize your
 * header views in the Fragment/Activity and add them to the adapter in the same way you used to
 * add them to a ListView.
 * </p>
 * <p>
 * I also required to be able to swap out multiple adapters with different content, therefore
 * setAdapter may be called multiple times.
 * </p>
 * Created by darnmason on 07/11/2014.
 */

public class PullLoadAndRefreshRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int HEADERS_START = Integer.MIN_VALUE;
    private static final int FOOTERS_START = Integer.MIN_VALUE + 10000;
    private static final int ITEMS_START = Integer.MIN_VALUE + 20000;
    private static final int ADAPTER_MAX_TYPES = 100;

    private RecyclerView.Adapter wrappedAdapter;
    private List<View> headerViews;
    private List<View> footerViews;
    private Map<Class, Integer> itemTypesOffset;

    /**
     * Construct a new header view recycler adapter
     *
     * @param adapter The underlying adapter to wrap
     */
    public PullLoadAndRefreshRecyclerViewAdapter(RecyclerView.Adapter adapter) {
        headerViews = new ArrayList<>();
        footerViews = new ArrayList<>();
        itemTypesOffset = new HashMap<>();
        setWrappedAdapter(adapter);
    }

    /**
     * Replaces the underlying adapter, notifying RecyclerView of changes
     *
     * @param adapter The new adapter to wrap
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (wrappedAdapter != null && wrappedAdapter.getItemCount() > 0) {
            notifyItemRangeRemoved(getHeaderCount(), wrappedAdapter.getItemCount());
        }
        setWrappedAdapter(adapter);
        notifyItemRangeInserted(getHeaderCount(), wrappedAdapter.getItemCount());
    }

    @Override
    public int getItemViewType(int position) {
        int hCount = getHeaderCount();
        if (position < hCount) {
            return HEADERS_START + position;
        } else {
            int itemCount = wrappedAdapter.getItemCount();
            if (position < hCount + itemCount) {
                return getAdapterTypeOffset() + wrappedAdapter.getItemViewType(position - hCount);
            } else {
                return FOOTERS_START + position - hCount - itemCount;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType < HEADERS_START + getHeaderCount())
            return new HeaderViewHolder(headerViews.get(viewType - HEADERS_START));
        else if (viewType < FOOTERS_START + getFooterCount())
            return new FooterViewHolder(footerViews.get(viewType - FOOTERS_START));
        else {
            return wrappedAdapter.onCreateViewHolder(viewGroup, viewType - getAdapterTypeOffset());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int headerCount = getHeaderCount();
        int itemCount = wrappedAdapter.getItemCount();
        if (position < headerCount) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
            ViewGroup.LayoutParams lp = headerViewHolder.itemView.getLayoutParams();
            RecyclerView recyclerView = (RecyclerView) headerViewHolder.itemView.getParent();
            lp.width = recyclerView.getWidth() - recyclerView.getPaddingLeft() - recyclerView.getPaddingRight();
            headerViewHolder.itemView.setLayoutParams(lp);
        } else if (position < headerCount + itemCount) {
            wrappedAdapter.onBindViewHolder(viewHolder, position - headerCount);
        } else {
            FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
            ViewGroup.LayoutParams lp = footerViewHolder.itemView.getLayoutParams();
            RecyclerView recyclerView = (RecyclerView) footerViewHolder.itemView.getParent();
            lp.width = recyclerView.getWidth() - recyclerView.getPaddingLeft() - recyclerView.getPaddingRight();
            footerViewHolder.itemView.setLayoutParams(lp);
        }
    }

    /**
     * Add a static view to appear at the start of the RecyclerView. Headers are displayed in the
     * order they were added.
     *
     * @param view The header view to add
     */
    public void addHeaderView(View view) {
        headerViews.add(view);
    }

    /**
     * Add a static view to appear at the end of the RecyclerView. Footers are displayed in the
     * order they were added.
     *
     * @param view The footer view to add
     */
    public void addFooterView(View view) {
        footerViews.add(view);
    }

    @Override
    public int getItemCount() {
        return getHeaderCount() + getFooterCount() + getWrappedItemCount();
    }

    /**
     * @return The item count in the underlying adapter
     */
    public int getWrappedItemCount() {
        return wrappedAdapter.getItemCount();
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

    private void setWrappedAdapter(RecyclerView.Adapter adapter) {
        if (wrappedAdapter != null) {
            wrappedAdapter.unregisterAdapterDataObserver(mDataObserver);
        }
        wrappedAdapter = adapter;
        Class adapterClass = wrappedAdapter.getClass();
        if (!itemTypesOffset.containsKey(adapterClass)) {
            putAdapterTypeOffset(adapterClass);
        }
        wrappedAdapter.registerAdapterDataObserver(mDataObserver);
    }

    public RecyclerView.Adapter getWrappedAdapter() {
        return wrappedAdapter;
    }

    private void putAdapterTypeOffset(Class adapterClass) {
        itemTypesOffset.put(adapterClass, ITEMS_START + itemTypesOffset.size() * ADAPTER_MAX_TYPES);
    }

    private int getAdapterTypeOffset() {
        return itemTypesOffset.get(wrappedAdapter.getClass());
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
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
            int hCount = getHeaderCount();
            notifyItemRangeChanged(fromPosition + hCount, toPosition + hCount + itemCount);
        }
    };
}