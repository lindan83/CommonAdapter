package com.lance.common.adapter.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import com.lance.common.adapter.demo.data.DataDAO;
import com.lance.common.adapterview.adapter.CommonAdapter;
import com.lance.common.adapterview.adapter.ViewHolder;

import java.util.List;

public class GridViewDemoActivity extends AppCompatActivity {
    private GridView mGvDemo;
    private CommonAdapter<String> mAdapter;
    private List<String> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view_demo);
        mGvDemo = (GridView) findViewById(R.id.gv_demo);
        mData = DataDAO.getData();
        mAdapter = new CommonAdapter<String>(this, R.layout.item_list, mData) {

            @Override
            protected void convert(ViewHolder viewHolder, String item, int position) {
                viewHolder.setText(R.id.tv_item, item + ", position = " + position);
            }
        };
        mGvDemo.setAdapter(mAdapter);
    }
}
