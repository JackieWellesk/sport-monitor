package com.hegel.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class R<T> {
    private int code; // 0 ok, -1 fail
    private String msg;
    private T data;

    public static <T> R<T> ok(T data) { return new R<>(0, "ok", data); }
    public static <T> R<T> fail(String msg) { return new R<>(-1, msg, null); }
}
