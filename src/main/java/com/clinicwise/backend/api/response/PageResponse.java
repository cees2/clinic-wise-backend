package com.clinicwise.backend.api.response;

import java.util.List;

public class PageResponse<T> extends ApiResponse<List<T>> {
    private boolean hasMore;

    public PageResponse(List<T> data, boolean hasMore){
        super(data);
        this.hasMore = hasMore;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public static <U> PageResponse<U> toResponse(List<U> data, boolean hasMore){
        return new PageResponse<>(data, hasMore);
    }
}
