package com.yameizitd.gateway.spoiler.exception.impl;

import com.yameizitd.gateway.spoiler.exception.BusinessException;

import java.io.Serial;

public class IllegalValueException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 6953199808705401011L;

    public IllegalValueException() {
        super();
    }

    public IllegalValueException(String message) {
        super(message);
    }

    public IllegalValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalValueException(Throwable cause) {
        super(cause);
    }
}
