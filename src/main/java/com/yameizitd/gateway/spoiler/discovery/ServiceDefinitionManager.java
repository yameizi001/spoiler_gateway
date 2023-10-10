package com.yameizitd.gateway.spoiler.discovery;

import com.yameizitd.gateway.spoiler.domain.business.ServiceDefinition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ServiceDefinitionManager {
    boolean supportable(ServiceDefinition serviceDefinition);

    Mono<Void> saveServices(Flux<ServiceDefinition> serviceDefinitions);

    Mono<Void> deleteServices(Flux<String> serviceIds);
}
