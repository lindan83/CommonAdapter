package com.lance.common.adapter.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.lance.common.adapter.demo.data.DataDAO;
import com.lance.common.adapter.demo.data.ItemBean;
import com.lance.common.adapterview.adapter.MultiItemTypeAdapter;
import com.lance.common.adapterview.adapter.ViewHolder;
import com.lance.common.adapterview.adapter.base.ItemViewDelegate;

import java.util.List;

public class ListViewMultiItemTypeDemoActivity extends AppCompatActivity {
    private ListView mLvDemo;
    private List<ItemBean> mData;
    private MultiItemTypeAdapter<ItemBean> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_demo);

        mLvDemo = (ListView) findViewById(R.id.lv_demo);
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
            public void convert(ViewHolder holder, ItemBean itemBean, int position) {
                holder.setText(R.id.tv_item_left, itemBean.getValue());
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
            public void convert(ViewHolder holder, ItemBean itemBean, int position) {
                holder.setText(R.id.tv_item_right, itemBean.getValue());
            }
        });
        mLvDemo.setAdapter(mAdapter);
    }
}
