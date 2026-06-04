package com.clinicwise.backend.common.list;

public class BaseFilter {
    private int size = 20;
    private int page = 0;
    private String sort;

    public BaseFilter(int size, int page, String sort) {
        this.size = size;
        this.page = page;
        this.sort = sort;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
