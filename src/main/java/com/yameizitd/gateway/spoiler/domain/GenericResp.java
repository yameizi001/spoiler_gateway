package com.yameizitd.gateway.spoiler.domain;

import java.io.Serial;
import java.io.Serializable;

public record GenericResp<T>(int code, String msg, T data) implements Serializable {
    @Serial
    private static final long serialVersionUID = -395239447375182093L;

    public static <T> GenericResp<T> ok(T data) {
        return resp(RespEnum.OK, data);
    }

    public static <T> GenericResp<T> resp(RespEnum resp, T data) {
        return resp(resp.getCode(), resp.getMsg(), data);
    }

    public static <T> GenericResp<T> resp(int code, String msg, T data) {
        return new GenericResp<>(code, msg, data);
    }
}
