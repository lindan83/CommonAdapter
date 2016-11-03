package com.lance.common.adapter.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lance.common.adapter.demo.data.DataDAO;
import com.lance.common.adapter.demo.data.ItemBean;
import com.lance.common.recyclerview.adapter.MultiItemTypeAdapter;
import com.lance.common.recyclerview.adapter.base.CommonRecyclerViewHolder;
import com.lance.common.recyclerview.adapter.base.ItemViewDelegate;

import java.util.List;

public class RecyclerViewMultiItemTypeDemoActivity extends AppCompatActivity {
    private RecyclerView mRvDemo;
    private List<ItemBean> mData;
    private MultiItemTypeAdapter<ItemBean> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_common_use_demo);

        mRvDemo = (RecyclerView) findViewById(R.id.rv_demo);
        mRvDemo.setItemAnimator(new DefaultItemAnimator());
        mRvDemo.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRvDemo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mData = DataDAO.getBeanListData();
        mAdapter = new MultiItemTypeAdapter<>(this, mData);
        mAdapter.addItemViewDelegate(new ItemViewDelegate<ItemBean>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_list_left;
            }

            @Override
            public boolean isForViewType(ItemBean item, int position) {
                return item.isLeft();
            }

            @Override
            public void convert(CommonRecyclerViewHolder holder, ItemBean itemBean, int position) {
                holder.setText(R.id.tv_item_left, itemBean.getValue() + ", " + position);
            }
        });
        mAdapter.addItemViewDelegate(new ItemViewDelegate<ItemBean>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_list_right;
            }

            @Override
            public boolean isForViewType(ItemBean item, int position) {
                return !item.isLeft();
            }

            @Override
            public void convert(CommonRecyclerViewHolder holder, ItemBean itemBean, int position) {
                holder.setText(R.id.tv_item_right, itemBean.getValue() + ", " + position);
            }
        });
        mRvDemo.setAdapter(mAdapter);
    }
}
