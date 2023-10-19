package com.yameizitd.gateway.spoiler.route.impl;

import com.yameizitd.gateway.spoiler.eventbus.EventListener;
import com.yameizitd.gateway.spoiler.eventbus.RouteRefreshEvent;
import com.yameizitd.gateway.spoiler.route.AbstractRouteDefinitionManager;
import com.yameizitd.gateway.spoiler.route.RouteDefinitionChecker;
import com.yameizitd.gateway.spoiler.route.RouteDefinitionLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class InMemoryRouteDefinitionLocator extends AbstractRouteDefinitionManager implements
        ApplicationEventPublisherAware, RouteDefinitionLocator, EventListener<RouteRefreshEvent>, InitializingBean,
        DisposableBean {
    private final Map<String, RouteDefinition> cache = new HashMap<>(256);
    private final RouteDefinitionChecker routeDefinitionChecker;
    private ApplicationEventPublisher applicationEventPublisher;
    private final RouteDefinitionLoader routeDefinitionLoader;
    private final ScheduledExecutorService executor;

    // cyclic dependence
    public InMemoryRouteDefinitionLocator(RouteDefinitionChecker routeDefinitionChecker,
                                          RouteDefinitionLoader routeDefinitionLoader) {
        this.routeDefinitionChecker = routeDefinitionChecker;
        this.routeDefinitionLoader = routeDefinitionLoader;
        this.executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("in-memory-route-refresh");
            return thread;
        });
    }

    @Override
    public void afterPropertiesSet() {
        executor.scheduleWithFixedDelay(() -> {
            // periodic refresh
            Map<String, RouteDefinition> routes = routeDefinitionLoader.refresh();
            cache.clear();
            cache.putAll(routes);
            refreshRoutes();
            log.debug("Refresh routes from the loader, total: {}", cache.size());
        }, 10L, 900L, TimeUnit.SECONDS);
    }

    @Override
    public void destroy() {
        executor.shutdown();
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
