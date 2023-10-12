package com.yameizitd.gateway.spoiler.exception.impl;

import com.yameizitd.gateway.spoiler.exception.BusinessException;

import java.io.Serial;

public class EntryNotExistException extends BusinessException {
    @Serial
    private static final long serialVersionUID = -6620817886620525346L;

    public EntryNotExistException() {
        super();
    }

    public EntryNotExistException(String message) {
        super(message);
    }

    public EntryNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntryNotExistException(Throwable cause) {
        super(cause);
    }
}
