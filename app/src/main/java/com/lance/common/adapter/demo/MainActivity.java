package com.lance.common.adapter.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showDemo(View v) {
        switch (v.getId()) {
            case R.id.btn_list_view_demo:
                startActivity(new Intent(this, ListViewDemoActivity.class));
                break;
            case R.id.btn_multi_item_type_list_view_demo:
                startActivity(new Intent(this, ListViewMultiItemTypeDemoActivity.class));
                break;
            case R.id.btn_grid_view_demo:
                startActivity(new Intent(this, GridViewDemoActivity.class));
                break;
            case R.id.btn_recycler_view_common_use_demo:
                startActivity(new Intent(this, RecyclerViewCommonUseDemoActivity.class));
                break;
            case R.id.btn_recycler_view_multi_type_item_demo:
                startActivity(new Intent(this, RecyclerViewMultiItemTypeDemoActivity.class));
                break;
            case R.id.btn_recycler_view_load_more_demo:
                startActivity(new Intent(this, RecyclerViewLoadMoreDemoActivity.class));
                break;
        }
    }
}
