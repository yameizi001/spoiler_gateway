package com.yameizitd.gateway.spoiler.exception.impl;

import com.yameizitd.gateway.spoiler.exception.ApplicationException;

import java.io.Serial;

public class FileInvalidException extends ApplicationException {
    @Serial
    private static final long serialVersionUID = -1540768322385196321L;

    public FileInvalidException() {
        super();
    }

    public FileInvalidException(String message) {
        super(message);
    }

    public FileInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileInvalidException(Throwable cause) {
        super(cause);
    }
}
