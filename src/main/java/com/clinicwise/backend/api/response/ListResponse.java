package com.clinicwise.backend.api.response;

import java.util.List;

public class ListResponse<T> extends ApiResponse<List<T>> {
    private long size;

    public ListResponse(List<T> data, long size){
        super(data);
        this.size = size;
    }

    public long getPage() {
        return size;
    }

    public void setPage(long size) {
        this.size = size;
    }

    public static <U> ListResponse<U> toResponse(List<U> data, long size){
        return new ListResponse<>(data, size);
    }
}
