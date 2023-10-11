package com.yameizitd.gateway.spoiler.route;

import com.yameizitd.gateway.spoiler.eventbus.EventListener;
import com.yameizitd.gateway.spoiler.eventbus.RefreshEvent;
import com.yameizitd.gateway.spoiler.eventbus.RouteRefreshEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public abstract class AbstractRouteDefinitionManager implements RouteDefinitionManager,
        EventListener<RouteRefreshEvent> {
    @Override
    public Mono<Void> onEvent(Mono<RouteRefreshEvent> event) {
        return event.flatMap(routeRefreshEvent -> {
            log.debug("On route refresh event: {}", routeRefreshEvent);
            RefreshEvent.Operation operation = routeRefreshEvent.getOperation();
            if (operation != null) {
                switch (operation) {
                    case SAVE_ROUTES -> {
                        Flux<RouteDefinition> routeDefinitions = Flux.fromStream(
                                routeRefreshEvent.getRouteDefinitions().stream()
                                        .filter(this::supportable)
                        );
                        return save(routeDefinitions);
                    }
                    case DELETE_ROUTES -> {
                        Flux<String> routeIds = Flux.fromStream(
                                routeRefreshEvent.getRouteDefinitions().stream()
                                        .map(RouteDefinition::getId)
                                        .filter(StringUtils::hasLength)
                        );
                        return delete(routeIds);
                    }
                    default -> {
                        log.warn("Not support the operation of route refresh event: {}", operation);
                        return Mono.error(new IllegalArgumentException(
                                "Operation of route refresh event not supported"
                        ));
                    }
                }
            }
            return Mono.error(new IllegalArgumentException("Operation of route refresh event should not be null"));
        }).then();
    }
}
