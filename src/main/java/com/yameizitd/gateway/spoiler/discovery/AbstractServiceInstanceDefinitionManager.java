package com.yameizitd.gateway.spoiler.discovery;

import com.yameizitd.gateway.spoiler.domain.business.ServiceDefinition;
import com.yameizitd.gateway.spoiler.eventbus.EventListener;
import com.yameizitd.gateway.spoiler.eventbus.InstanceRefreshEvent;
import com.yameizitd.gateway.spoiler.eventbus.RefreshEvent;
import com.yameizitd.gateway.spoiler.eventbus.ServiceRefreshEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public abstract class AbstractServiceInstanceDefinitionManager implements ServiceDefinitionManager,
        InstanceDefinitionManager, EventListener<RefreshEvent> {
    @Override
    public Mono<Void> onEvent(Mono<RefreshEvent> event) {
        return event.flatMap(originEvent -> {
            if (originEvent instanceof ServiceRefreshEvent serviceRefreshEvent) {
                log.debug("On service refresh event: {}", serviceRefreshEvent);
                return handleServiceRefreshEvent(serviceRefreshEvent);
            } else if (originEvent instanceof InstanceRefreshEvent instanceRefreshEvent) {
                log.debug("On instance refresh event: {}", instanceRefreshEvent);
                return handleInstanceRefreshEvent(instanceRefreshEvent);
            } else {
                log.trace("Not supported refresh event type in service instance manager: " + originEvent);
                return Mono.empty();
            }
        }).then();
    }

    private Mono<Void> handleServiceRefreshEvent(ServiceRefreshEvent serviceRefreshEvent) {
        RefreshEvent.Operation operation = serviceRefreshEvent.getOperation();
        if (operation != null) {
            switch (operation) {
                case SAVE_SERVICES -> {
                    Flux<ServiceDefinition> serviceDefinitions = Flux.fromStream(
                            serviceRefreshEvent.getServiceDefinitions().stream()
                                    .filter(this::supportable)
                    );
                    return saveServices(serviceDefinitions);
                }
                case DELETE_SERVICES -> {
                    Flux<String> serviceIds = Flux.fromStream(
                            serviceRefreshEvent.getServiceDefinitions().stream()
                                    .filter(this::supportable)
                                    .map(ServiceDefinition::getId)
                    );
                    return deleteServices(serviceIds);
                }
                default -> {
                    log.warn("Not support the operation of service refresh event: {}", operation);
                    return Mono.error(new IllegalArgumentException("Operation of service refresh event not supported"));
                }
            }
        }
        return Mono.error(new IllegalArgumentException("Operation of service refresh event should not be null"));
    }

    private Mono<Void> handleInstanceRefreshEvent(InstanceRefreshEvent instanceRefreshEvent) {
        RefreshEvent.Operation operation = instanceRefreshEvent.getOperation();
        if (operation != null) {
            switch (operation) {
                case SAVE_INSTANCES -> {
                    Flux<ServiceInstance> instanceDefinitions = Flux.fromStream(
                            instanceRefreshEvent.getServiceInstances().stream()
                                    .filter(this::supportable)
                    );
                    return saveInstances(instanceDefinitions);
                }
                case DELETE_INSTANCES -> {
                    Flux<ServiceInstance> instanceDefinitions = Flux.fromStream(
                            instanceRefreshEvent.getServiceInstances().stream()
                                    .filter(this::supportable)
                    );
                    return deleteInstances(instanceDefinitions);
                }
                default -> {
                    log.warn("Not support the operation of instance refresh event: {}", operation);
                    return Mono.error(new IllegalArgumentException("Operation of instance refresh event not supported"));
                }
            }
        }
        return Mono.error(new IllegalArgumentException("Operation of instance refresh event should not be null"));
    }
}
