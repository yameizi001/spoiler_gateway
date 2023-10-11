package com.yameizitd.gateway.spoiler.route.impl;

import com.yameizitd.gateway.spoiler.eventbus.EventListener;
import com.yameizitd.gateway.spoiler.eventbus.RouteRefreshEvent;
import com.yameizitd.gateway.spoiler.route.AbstractRouteDefinitionManager;
import com.yameizitd.gateway.spoiler.route.RouteDefinitionChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class InMemoryRouteDefinitionLocator extends AbstractRouteDefinitionManager implements
        ApplicationEventPublisherAware, RouteDefinitionLocator, EventListener<RouteRefreshEvent> {
    private final Map<String, RouteDefinition> cache = new HashMap<>(256);
    private final RouteDefinitionChecker routeDefinitionChecker;
    private ApplicationEventPublisher applicationEventPublisher;

    // cyclic dependence
    public InMemoryRouteDefinitionLocator(@Lazy RouteDefinitionChecker routeDefinitionChecker) {
        this.routeDefinitionChecker = routeDefinitionChecker;
    }

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return Flux.fromIterable(cache.values());
    }

    @Override
    public boolean supportable(RouteDefinition routeDefinition) {
        return routeDefinitionChecker.check(routeDefinition);
    }

    @Override
    public Mono<Void> save(Flux<RouteDefinition> routeDefinitions) {
        return routeDefinitions.flatMap(definition -> {
            // verified
            log.debug("Saving route definition: {}", definition);
            cache.put(definition.getId(), definition);
            return Mono.empty();
        }).then().doOnSuccess(v -> {
            log.debug("Cached routes: {}", cache.keySet());
            refreshRoutes();
        });
    }

    @Override
    public Mono<Void> delete(Flux<String> routeIds) {
        return routeIds.flatMap(id -> {
            log.debug("Deleting route definition: {}", id);
            cache.remove(id);
            return Mono.empty();
        }).then().doOnSuccess(v -> {
            log.debug("Cached routes: {}", cache.keySet());
            refreshRoutes();
        });
    }

    private void refreshRoutes() {
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
    }
}
