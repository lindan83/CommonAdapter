package com.lance.common.adapter.demo.data;

/**
 * Created by lindan on 16-11-2.
 */

public class ItemBean {
    private boolean left;
    private String value;

    public ItemBean(boolean left, String value) {
        this.left = left;
        this.value = value;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
