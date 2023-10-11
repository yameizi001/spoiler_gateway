package com.yameizitd.gateway.spoiler.route;

import org.springframework.cloud.gateway.route.RouteDefinition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RouteDefinitionManager {
    boolean supportable(RouteDefinition routeDefinition);

    Mono<Void> save(Flux<RouteDefinition> routeDefinitions);

    Mono<Void> delete(Flux<String> routeIds);
}
