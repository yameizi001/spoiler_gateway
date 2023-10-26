package com.yameizitd.gateway.spoiler.handler.impl;

import com.yameizitd.gateway.spoiler.domain.entity.InstanceEntity;
import com.yameizitd.gateway.spoiler.domain.entity.RouteEntity;
import com.yameizitd.gateway.spoiler.domain.entity.ServiceEntity;
import com.yameizitd.gateway.spoiler.eventbus.*;
import com.yameizitd.gateway.spoiler.handler.EventHandler;
import com.yameizitd.gateway.spoiler.mapstruct.InstanceMapstruct;
import com.yameizitd.gateway.spoiler.mapstruct.RouteMapstruct;
import com.yameizitd.gateway.spoiler.mapstruct.ServiceMapstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class EventHandlerImpl implements EventHandler {
    private final ServiceMapstruct serviceMapstruct;
    private final InstanceMapstruct instanceMapstruct;
    private final RouteMapstruct routeMapstruct;
    private final EventPublisher eventPublisher;

    public EventHandlerImpl(ServiceMapstruct serviceMapstruct,
                            InstanceMapstruct instanceMapstruct,
                            RouteMapstruct routeMapstruct,
                            EventPublisher eventPublisher) {
        this.serviceMapstruct = serviceMapstruct;
        this.instanceMapstruct = instanceMapstruct;
        this.routeMapstruct = routeMapstruct;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    @Override
    public <T> void publish(RefreshEvent.Operation operation, T entity) {
        publish(operation, List.of(entity));
    }

    @Transactional
    @Override
    public void publish(RefreshEvent.Operation operation, List<?> entities) {
        if (entities != null && !entities.isEmpty()) {
            Event event = buildEvent(operation, entities);
            eventPublisher.publish(Mono.just(event)).block(Duration.of(2L, TimeUnit.SECONDS.toChronoUnit()));
        }
    }

    private Event buildEvent(RefreshEvent.Operation operation, List<?> entities) {
        return switch (operation) {
            case SAVE_SERVICES, DELETE_SERVICES -> new ServiceRefreshEvent(
                    operation,
                    entities.stream()
                            .map(entity -> (ServiceEntity) entity)
                            .map(serviceMapstruct::entity2definition)
                            .toList()
            );
            case SAVE_INSTANCES, DELETE_INSTANCES -> new InstanceRefreshEvent(
                    operation,
                    entities.stream()
                            .map(entity -> (InstanceEntity) entity)
                            .map(instanceMapstruct::entity2definition)
                            .toList()
            );
            case SAVE_ROUTES, DELETE_ROUTES -> new RouteRefreshEvent(
                    operation,
                    entities.stream()
                            .map(entity -> (RouteEntity) entity)
                            .map(routeMapstruct::entity2definition)
                            .toList()
            );
        };
    }
}
