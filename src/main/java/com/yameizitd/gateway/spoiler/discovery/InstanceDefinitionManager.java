package com.yameizitd.gateway.spoiler.discovery;

import org.springframework.cloud.client.ServiceInstance;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InstanceDefinitionManager {
    boolean supportable(ServiceInstance serviceInstance);

    Mono<Void> saveInstances(Flux<ServiceInstance> serviceInstances);

    Mono<Void> deleteInstances(Flux<ServiceInstance> serviceInstances);
}
