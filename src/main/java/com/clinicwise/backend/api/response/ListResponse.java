package com.clinicwise.backend.api.response;

import java.util.List;

public class ListResponse<T> extends ApiResponse<List<T>> {
    private boolean hasNext;

    public ListResponse(List<T> data, boolean hasNext){
        super(data);
        this.hasNext = hasNext;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public static <U> ListResponse<U> toResponse(List<U> data, boolean hasNext){
        return new ListResponse<>(data, hasNext);
    }
}
