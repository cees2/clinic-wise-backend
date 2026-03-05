package com.clinicwise.backend.api.response;

public class ApiResponse<T> {
    private T data;

    public ApiResponse(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <U> ApiResponse<U> toResponse(U data){
        return new ApiResponse<>(data);
    }
}
