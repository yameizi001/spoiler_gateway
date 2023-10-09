package com.yameizitd.gateway.spoiler.exception.impl;

import com.yameizitd.gateway.spoiler.exception.ApplicationException;

import java.io.Serial;

public class TypeConvertException extends ApplicationException {
    @Serial
    private static final long serialVersionUID = 2956603350871584810L;

    public TypeConvertException() {
        super();
    }

    public TypeConvertException(String message) {
        super(message);
    }

    public TypeConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeConvertException(Throwable cause) {
        super(cause);
    }
}
