package com.lance.common.adapter.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lance.common.adapter.demo.data.DataDAO;
import com.lance.common.recyclerview.adapter.CommonRecyclerViewAdapter;
import com.lance.common.recyclerview.adapter.base.CommonRecyclerViewHolder;

import java.util.List;

public class RecyclerViewCommonUseDemoActivity extends AppCompatActivity {
    private RecyclerView mRvDemo;
    private List<String> mData;
    private CommonRecyclerViewAdapter<String> mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_common_use_demo);

        mRvDemo = (RecyclerView) findViewById(R.id.rv_demo);
        mRvDemo.setItemAnimator(new DefaultItemAnimator());
        mRvDemo.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRvDemo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mData = DataDAO.getData();
        mAdapter = new CommonRecyclerViewAdapter<String>(this, R.layout.item_list, mData) {
            @Override
            protected void convert(CommonRecyclerViewHolder holder, String s, int position) {
                holder.setText(R.id.tv_item, s + ", position = " + position);
            }
        };
        mRvDemo.setAdapter(mAdapter);
    }
}
