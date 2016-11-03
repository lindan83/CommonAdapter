package com.lance.common.recyclerview.adapter.base;

/**
 * Created by lindan on 16-11-2.
 */

public interface ItemViewDelegate<T> {
    int getItemViewLayoutId();

    boolean isForViewType(T item, int position);

    void convert(CommonRecyclerViewHolder holder, T t, int position);
}
