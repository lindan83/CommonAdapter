package com.lance.common.adapter.demo.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by lindan on 16-11-2.
 */

public class DataDAO {
    public static List<String> getData() {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            data.add("item " + (i + 1));
        }
        return data;
    }

    public static List<String> getData(int pageIndex, int pageSize) {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < pageSize; i++) {
            data.add("item " + ((pageIndex - 1) * pageSize + i + 1));
        }
        return data;
    }

    public static List<ItemBean> getBeanListData() {
        List<ItemBean> data = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            ItemBean itemBean = new ItemBean(random.nextBoolean(), "item " + (i + random.nextInt(20) + 1));
            data.add(itemBean);
        }
        return data;
    }
}
