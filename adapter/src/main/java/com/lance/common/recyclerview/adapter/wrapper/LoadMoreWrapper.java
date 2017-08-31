package com.lance.common.recyclerview.adapter.wrapper;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lance.common.recyclerview.adapter.base.CommonRecyclerViewHolder;
import com.lance.common.recyclerview.adapter.utils.WrapperUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 加载更多Footer
 */
public class LoadMoreWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ITEM_TYPE_LOAD_FAILED_VIEW = Integer.MAX_VALUE - 1;
    public static final int ITEM_TYPE_NO_MORE_VIEW = Integer.MAX_VALUE - 2;
    public static final int ITEM_TYPE_LOAD_MORE_VIEW = Integer.MAX_VALUE - 3;
    public static final int ITEM_TYPE_NO_VIEW = Integer.MAX_VALUE - 4;//不展示footer view

    private Context context;
    private RecyclerView.Adapter innerAdapter;

    private View loadMoreView;
    private View loadMoreFailedView;
    private View noMoreView;

    private int currentItemType = ITEM_TYPE_LOAD_MORE_VIEW;
    private LoadMoreScrollListener loadMoreScrollListener;


    private boolean isLoadError = false;//标记是否加载出错
    private boolean isHaveStatesView = true;

    /**
     * 加载更多监听器
     */
    public interface OnLoadListener {
        /**
         * 加载更多重试
         */
        void onRetry();

        /**
         * 加载更多
         */
        void onLoadMore();
    }

    private OnLoadListener onLoadListener;

    public LoadMoreWrapper(Context context, RecyclerView.Adapter adapter) {
        this.context = context;
        this.innerAdapter = adapter;
        loadMoreScrollListener = new LoadMoreScrollListener() {
            @Override
            public void loadMore() {
                if (onLoadListener != null && isHaveStatesView) {
                    if (!isLoadError) {
                        showLoadMore();
                        onLoadListener.onLoadMore();
                    }
                }
            }
        };
    }

    /**
     * 显示加载更多
     */
    public void showLoadMore() {
        currentItemType = ITEM_TYPE_LOAD_MORE_VIEW;
        isLoadError = false;
        isHaveStatesView = true;
        notifyItemChanged(getItemCount());
    }

    /**
     * 显示加载失败
     */
    public void showLoadError() {
        currentItemType = ITEM_TYPE_LOAD_FAILED_VIEW;
        isLoadError = true;
        isHaveStatesView = true;
        notifyItemChanged(getItemCount());
    }

    /**
     * 显示加载完成
     */
    public void showLoadComplete() {
        currentItemType = ITEM_TYPE_NO_MORE_VIEW;
        isLoadError = false;
        isHaveStatesView = true;
        notifyItemChanged(getItemCount());
    }

    /**
     * 不显示加载View
     */
    public void disableLoadMore() {
        currentItemType = ITEM_TYPE_NO_VIEW;
        isHaveStatesView = false;
        notifyDataSetChanged();
    }

    //获取正在加载中ViewHolder
    private RecyclerView.ViewHolder getLoadMoreViewHolder() {
        if (loadMoreView == null) {
            loadMoreView = new TextView(context);
            loadMoreView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            loadMoreView.setPadding(20, 20, 20, 20);
            ((TextView) loadMoreView).setText("正在加载中");
            ((TextView) loadMoreView).setGravity(Gravity.CENTER);
        }
        return CommonRecyclerViewHolder.createViewHolder(context, loadMoreView);
    }

    //获取加载失败View
    private RecyclerView.ViewHolder getLoadFailedViewHolder() {
        if (loadMoreFailedView == null) {
            loadMoreFailedView = new TextView(context);
            loadMoreFailedView.setPadding(20, 20, 20, 20);
            loadMoreFailedView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            ((TextView) loadMoreFailedView).setText("加载失败，请点我重试");
            ((TextView) loadMoreFailedView).setGravity(Gravity.CENTER);
        }
        return CommonRecyclerViewHolder.createViewHolder(context, loadMoreFailedView);
    }

    //获取没有更多数据View
    private RecyclerView.ViewHolder getNoMoreViewHolder() {
        if (noMoreView == null) {
            noMoreView = new TextView(context);
            noMoreView.setPadding(20, 20, 20, 20);
            noMoreView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            ((TextView) noMoreView).setText("没有更多了");
            ((TextView) noMoreView).setGravity(Gravity.CENTER);
        }
        return CommonRecyclerViewHolder.createViewHolder(context, noMoreView);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1 && isHaveStatesView) {
            return currentItemType;
        }
        return innerAdapter.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_NO_MORE_VIEW) {
            return getNoMoreViewHolder();
        } else if (viewType == ITEM_TYPE_LOAD_MORE_VIEW) {
            return getLoadMoreViewHolder();
        } else if (viewType == ITEM_TYPE_LOAD_FAILED_VIEW) {
            return getLoadFailedViewHolder();
        }
        return innerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == ITEM_TYPE_LOAD_FAILED_VIEW) {
            loadMoreFailedView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onLoadListener != null) {
                        onLoadListener.onRetry();
                        showLoadMore();
                    }
                }
            });
            return;
        }
        if (!isFooterType(holder.getItemViewType()))
            innerAdapter.onBindViewHolder(holder, position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        WrapperUtils.onAttachedToRecyclerView(innerAdapter, recyclerView, new WrapperUtils.SpanSizeCallback() {
            @Override
            public int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position) {
                if (position == getItemCount() - 1 && isHaveStatesView) {
                    return layoutManager.getSpanCount();
                }
                if (oldLookup != null && isHaveStatesView) {
                    return oldLookup.getSpanSize(position);
                }
                return 1;
            }
        });
        recyclerView.addOnScrollListener(loadMoreScrollListener);
    }


    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        innerAdapter.onViewAttachedToWindow(holder);

        if (holder.getLayoutPosition() == getItemCount() - 1 && isHaveStatesView) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();

            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;

                p.setFullSpan(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        return innerAdapter.getItemCount() + (isHaveStatesView ? 1 : 0);
    }

    private boolean isFooterType(int type) {
        return type == ITEM_TYPE_NO_VIEW ||
                type == ITEM_TYPE_LOAD_FAILED_VIEW ||
                type == ITEM_TYPE_NO_MORE_VIEW ||
                type == ITEM_TYPE_LOAD_MORE_VIEW;
    }

    /**
     * 设置自定义LoadMoreView，如果没有设置的，将使用默认的View
     */
    public LoadMoreWrapper setLoadMoreView(View loadMoreView) {
        this.loadMoreView = loadMoreView;
        return this;
    }

    /**
     * 设置自定义LoadMoreFailedView，如果没有设置的，将使用默认的View
     */
    public LoadMoreWrapper setLoadMoreFailedView(View loadMoreFailedView) {
        this.loadMoreFailedView = loadMoreFailedView;
        return this;
    }

    /**
     * 设置自定义NoMoreView，如果没有设置的，将使用默认的View
     */
    public LoadMoreWrapper setNoMoreView(View noMoreView) {
        this.noMoreView = noMoreView;
        return this;
    }

    public LoadMoreWrapper setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
        return this;
    }
}