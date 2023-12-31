package com.yameizitd.gateway.spoiler.discovery.impl;

import com.yameizitd.gateway.spoiler.discovery.AbstractServiceInstanceDefinitionManager;
import com.yameizitd.gateway.spoiler.discovery.ServiceInstanceDefinitionManager;
import com.yameizitd.gateway.spoiler.discovery.SysDiscoveryClientLoader;
import com.yameizitd.gateway.spoiler.domain.business.ServiceDefinition;
import com.yameizitd.gateway.spoiler.eventbus.EventListener;
import com.yameizitd.gateway.spoiler.eventbus.RefreshEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class InMemoryReactiveDiscoveryClient extends AbstractServiceInstanceDefinitionManager implements
        ServiceInstanceDefinitionManager, ReactiveDiscoveryClient, EventListener<RefreshEvent>, InitializingBean,
        DisposableBean {
    private final Map<String, List<ServiceInstance>> cache = new HashMap<>(256);
    private final SysDiscoveryClientLoader discoveryClientLoader;
    private final ScheduledExecutorService executor;

    public InMemoryReactiveDiscoveryClient(SysDiscoveryClientLoader discoveryClientLoader) {
        this.discoveryClientLoader = discoveryClientLoader;
        this.executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("in-memory-discover-refresh");
            return thread;
        });
    }

    @Override
    public void afterPropertiesSet() {
        executor.scheduleWithFixedDelay(() -> {
            // periodic refresh
            Map<String, List<ServiceInstance>> serviceInstances = discoveryClientLoader.refresh();
            cache.clear();
            cache.putAll(serviceInstances);
            log.debug("Refresh services from the loader, total: {}", cache.size());
        }, 10L, 900L, TimeUnit.SECONDS);
    }

    @Override
    public void destroy() {
        executor.shutdown();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public String description() {
        return "In Memory Reactive Discovery Client";
    }

    @Override
    public Flux<String> getServices() {
        return Flux.fromIterable(cache.keySet());
    }

    @Override
    public Flux<ServiceInstance> getInstances(String serviceId) {
        return Flux.fromIterable(cache.getOrDefault(serviceId, Collections.emptyList()));
    }

    @Override
    public boolean supportable(ServiceDefinition serviceDefinition) {
        return StringUtils.hasLength(serviceDefinition.getId());
    }

    @Override
    public boolean supportable(ServiceInstance serviceInstance) {
        String serviceId = serviceInstance.getServiceId();
        if (StringUtils.hasLength(serviceId)) {
            return cache.containsKey(serviceId);
        }
        return false;
    }

    @Override
    public Mono<Void> saveServices(Flux<ServiceDefinition> serviceDefinitions) {
        return serviceDefinitions.map(serviceDefinition -> {
                    log.debug("Saving service definition: {}", serviceDefinition);
                    String serviceId = serviceDefinition.getId();
                    synchronized (this) {
                        if (cache.containsKey(serviceId)) {
                            log.debug("Ignore, service already existed in cache: {}", serviceId);
                        } else {
                            List<ServiceInstance> emptyInstances = new ArrayList<>();
                            cache.put(serviceId, emptyInstances);
                        }
                    }
                    return serviceDefinition;
                })
                .then()
                .doOnSuccess(v -> log.debug("Cached services: {}", cache.keySet()));
    }

    @Override
    public Mono<Void> deleteServices(Flux<String> serviceIds) {
        return serviceIds.map(serviceId -> {
                    log.debug("Deleting service definition: {}", serviceId);
                    cache.remove(serviceId);
                    return serviceId;
                })
                .then()
                .doOnSuccess(v -> log.debug("Cached services: {}", cache.keySet()));
    }

    @Override
    public Mono<Void> saveInstances(Flux<ServiceInstance> serviceInstances) {
        return serviceInstances.map(serviceInstance -> {
                    log.debug("Saving instance definition: {}", serviceInstance);
                    String serviceId = serviceInstance.getServiceId();
                    synchronized (this) {
                        if (cache.containsKey(serviceId)) {
                            List<ServiceInstance> instances = cache.get(serviceId);
                            instances.removeIf(instance ->
                                    instance.getInstanceId().equals(serviceInstance.getInstanceId())
                            );
                            instances.add(serviceInstance);
                        } else {
                            List<ServiceInstance> instances = new ArrayList<>();
                            instances.add(serviceInstance);
                            cache.put(serviceId, instances);
                        }
                    }
                    return serviceInstance;
                })
                .doOnNext(serviceInstance ->
                        log.debug("Saved instance: {}", serviceInstance)
                )
                .then();
    }

    @Override
    public Mono<Void> deleteInstances(Flux<ServiceInstance> serviceInstances) {
        return serviceInstances.map(serviceInstance -> {
                    log.debug("Deleting instance definition: {}", serviceInstance);
                    String serviceId = serviceInstance.getServiceId();
                    synchronized (this) {
                        if (cache.containsKey(serviceId)) {
                            cache.get(serviceId).removeIf(instance ->
                                    instance.getInstanceId().equals(serviceInstance.getInstanceId())
                            );
                        } else {
                            log.debug("Ignore, service id of instance not in cache: {}", serviceId);
                        }
                    }
                    return serviceInstance;
                })
                .doOnNext(serviceInstance ->
                        log.debug("Removed instance: {}", serviceInstance)
                )
                .then();
    }
}
