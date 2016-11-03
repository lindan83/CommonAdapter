package com.lance.common.adapterview.adapter.base;

import com.lance.common.adapterview.adapter.ViewHolder;

public interface ItemViewDelegate<T> {
    int getItemViewLayoutId();

    boolean isForViewType(T item, int position);

    void convert(ViewHolder holder, T t, int position);
}
