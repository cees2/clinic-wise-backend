package com.clinicwise.backend.common.list.filter;

// TODO: Make abstract
public class BaseFilter {
    private int size;
    private int page;
    private String sort;
    private String search;

    public BaseFilter(int size, int page, String sort, String search) {
        this.size = size;
        this.page = page;
        this.sort = sort;
        this.search = search;
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

    public void setSort(String sort) {this.sort = sort;}

    public String getSearch() {return search;}

    public void setSearch(String search) {this.search = search;}
}
