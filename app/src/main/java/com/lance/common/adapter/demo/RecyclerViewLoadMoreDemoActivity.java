package com.lance.common.adapter.demo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lance.common.adapter.demo.data.DataDAO;
import com.lance.common.recyclerview.adapter.CommonRecyclerViewAdapter;
import com.lance.common.recyclerview.adapter.base.CommonRecyclerViewHolder;
import com.lance.common.recyclerview.adapter.wrapper.EmptyWrapper;
import com.lance.common.recyclerview.adapter.wrapper.HeaderAndFooterWrapper;
import com.lance.common.recyclerview.adapter.wrapper.LoadMoreWrapper;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewLoadMoreDemoActivity extends AppCompatActivity {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private List<String> mData = new ArrayList<>();
    private CommonRecyclerViewAdapter<String> mAdapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private EmptyWrapper mEmptyWrapper;
    private LoadMoreWrapper mLoadMoreWrapper;

    private int mPageIndex = 1;
    private final int mPageSize = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_load_more_demo);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(true);
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_demo);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mAdapter = new CommonRecyclerViewAdapter<String>(this, R.layout.item_list, mData) {
            @Override
            protected void convert(CommonRecyclerViewHolder holder, String s, int position) {
                holder.setText(R.id.tv_item, s + " : " + holder.getAdapterPosition() + " , " + holder.getLayoutPosition());
            }
        };
        initEmptyView();
        initHeaderAndFooter();

        mLoadMoreWrapper = new LoadMoreWrapper(mHeaderAndFooterWrapper);
        mLoadMoreWrapper.setLoadMoreView(R.layout.default_loading);
        mLoadMoreWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(mPageIndex > 3) {
                    return;
                }
                loadData(false);
            }
        });

        mRecyclerView.setAdapter(mLoadMoreWrapper);
        mAdapter.setOnItemClickListener(new CommonRecyclerViewAdapter.DefaultOnItemClickHandler() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Toast.makeText(RecyclerViewLoadMoreDemoActivity.this, "pos = " + position, Toast.LENGTH_SHORT).show();
            }
        });

        loadData(true);
    }

    private void initEmptyView() {
        mEmptyWrapper = new EmptyWrapper(mAdapter);
        mEmptyWrapper.setEmptyView(LayoutInflater.from(this).inflate(R.layout.empty_view, mRecyclerView, false));
    }

    private void initHeaderAndFooter() {
        if (mEmptyWrapper != null) {
            mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mEmptyWrapper);
        } else {
            mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mAdapter);
        }

        TextView t1 = (TextView) LayoutInflater.from(this).inflate(R.layout.layout_header, null);
        t1.setText("Header 1");
        TextView t2 = (TextView) LayoutInflater.from(this).inflate(R.layout.layout_footer, null);
        t2.setText("Footer 1");
        mHeaderAndFooterWrapper.addHeaderView(t1);
        mHeaderAndFooterWrapper.addFooterView(t2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recycler_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_linear:
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                break;
            case R.id.action_grid:
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                break;
            case R.id.action_staggered:
                mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                break;
            case R.id.action_clear_data:
                mData.clear();
                mAdapter.notifyDataSetChanged();
                break;
        }
        mRecyclerView.setAdapter(mLoadMoreWrapper);
        return super.onOptionsItemSelected(item);
    }

    private void loadData(boolean refresh) {
        if (refresh) {
            mPageIndex = 1;
            mData.clear();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mData.addAll(DataDAO.getData(mPageIndex++, mPageSize));
                mLoadMoreWrapper.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }
}