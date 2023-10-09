package com.yameizitd.gateway.spoiler.exception;

import java.io.Serial;

public abstract class BusinessException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 6167969446240497452L;

    public BusinessException() {
        super();
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }
}
