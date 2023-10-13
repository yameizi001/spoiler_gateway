package com.yameizitd.gateway.spoiler.exception.impl;

import com.yameizitd.gateway.spoiler.exception.BusinessException;

import java.io.Serial;

public class EntryInuseException extends BusinessException {
    @Serial
    private static final long serialVersionUID = -5733824984150847211L;

    public EntryInuseException() {
        super();
    }

    public EntryInuseException(String message) {
        super(message);
    }

    public EntryInuseException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntryInuseException(Throwable cause) {
        super(cause);
    }
}
