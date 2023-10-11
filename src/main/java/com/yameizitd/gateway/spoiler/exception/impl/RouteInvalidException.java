package com.yameizitd.gateway.spoiler.exception.impl;

import com.yameizitd.gateway.spoiler.exception.ApplicationException;

import java.io.Serial;

public class RouteInvalidException extends ApplicationException {
    @Serial
    private static final long serialVersionUID = 6790279241011607080L;

    public RouteInvalidException() {
        super();
    }

    public RouteInvalidException(String message) {
        super(message);
    }

    public RouteInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public RouteInvalidException(Throwable cause) {
        super(cause);
    }
}
