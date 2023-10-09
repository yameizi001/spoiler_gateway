package com.yameizitd.gateway.spoiler.exception;

import java.io.Serial;

public abstract class ApplicationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1321720405365090554L;

    public ApplicationException() {
        super();
    }

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }
}
