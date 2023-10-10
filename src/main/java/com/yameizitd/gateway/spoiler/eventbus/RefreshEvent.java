package com.yameizitd.gateway.spoiler.eventbus;

import lombok.*;

import java.io.Serial;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class RefreshEvent implements Event {
    @Serial
    private static final long serialVersionUID = 7552436234899839786L;

    private final Operation operation;

    public enum Operation {
        SAVE_SERVICES, DELETE_SERVICES,
        SAVE_INSTANCES, DELETE_INSTANCES,
        SAVE_ROUTE, DELETE_ROUTES
    }
}
