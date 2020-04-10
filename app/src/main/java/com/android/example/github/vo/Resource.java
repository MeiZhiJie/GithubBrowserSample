package com.android.example.github.vo;

import androidx.annotation.Nullable;

public class Resource<T> {
    private Status status;
    private T data;
    private String message;

    private Resource(Status status,
                    @Nullable T data,
                    @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource(Status.SUCCESS, data, null);
    }

    public static  <T> Resource<T> error(String msg, @Nullable T data) {
        return new Resource(Status.ERROR, data, msg);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource(Status.LOADING, data,null);
    }
}
