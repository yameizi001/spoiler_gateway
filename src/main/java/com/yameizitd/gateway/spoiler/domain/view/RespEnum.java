package com.yameizitd.gateway.spoiler.domain.view;

public enum RespEnum {
    OK(200, "success"),
    BAD_REQUEST(4000, "bad request"),
    UNKNOWN_ERROR(5000, "unknown error"),
    DATABASE_ERROR(5001, "database error"),
    BUSINESS_ERROR(5002, "business error"),
    ;

    private final int code;
    private final String msg;

    RespEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
