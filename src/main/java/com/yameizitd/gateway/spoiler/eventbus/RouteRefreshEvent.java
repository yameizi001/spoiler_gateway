package com.yameizitd.gateway.spoiler.eventbus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.io.Serial;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class RouteRefreshEvent extends RefreshEvent {
    @Serial
    private static final long serialVersionUID = 5544087336634908004L;

    private List<RouteDefinition> routeDefinitions;

    public RouteRefreshEvent(Operation operation, List<RouteDefinition> routeDefinitions) {
        super(operation);
        this.routeDefinitions = routeDefinitions;
    }
}
