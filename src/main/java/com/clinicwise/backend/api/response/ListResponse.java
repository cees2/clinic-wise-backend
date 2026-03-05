package com.clinicwise.backend.api.response;

public class ListResponse<T> extends ApiResponse<T> {
    private long size;

    public ListResponse(T data, long size){
        super(data);
        this.size = size;
    }

    public long getPage() {
        return size;
    }

    public void setPage(long size) {
        this.size = size;
    }

    public static <U> ListResponse<U> toResponse(U data, long size){
        return new ListResponse<>(data, size);
    }
}
