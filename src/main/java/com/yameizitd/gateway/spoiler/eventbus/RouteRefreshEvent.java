package com.yameizitd.gateway.spoiler.eventbus;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.io.Serial;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class RouteRefreshEvent extends RefreshEvent {
    @Serial
    private static final long serialVersionUID = 5544087336634908004L;

    private final List<RouteDefinition> routeDefinitions;

    public RouteRefreshEvent(RefreshEvent.Operation operation, List<RouteDefinition> routeDefinitions) {
        super(operation);
        this.routeDefinitions = routeDefinitions;
    }
}
