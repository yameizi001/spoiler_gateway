package com.yameizitd.gateway.spoiler.eventbus;

import lombok.*;

import java.io.Serial;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class RefreshEvent implements Event {
    @Serial
    private static final long serialVersionUID = 7552436234899839786L;

    private Operation operation;

    public enum Operation {
        SAVE_SERVICES, DELETE_SERVICES,
        SAVE_INSTANCES, DELETE_INSTANCES,
        SAVE_ROUTES, DELETE_ROUTES
    }
}
