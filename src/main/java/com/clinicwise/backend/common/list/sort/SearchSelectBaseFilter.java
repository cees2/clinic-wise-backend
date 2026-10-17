package com.clinicwise.backend.common.list.sort;

public class SearchSelectBaseFilter {
    private int page;
    private int size;
    private String search;

    public SearchSelectBaseFilter(int page, int size, String search) {
        this.page = page;
        this.size = size;
        this.search = search;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
