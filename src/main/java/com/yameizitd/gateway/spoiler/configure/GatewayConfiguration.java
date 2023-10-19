package com.yameizitd.gateway.spoiler.configure;

import com.yameizitd.gateway.spoiler.discovery.ServiceInstanceDefinitionManager;
import com.yameizitd.gateway.spoiler.discovery.SysDiscoveryClientLoader;
import com.yameizitd.gateway.spoiler.discovery.impl.InMemoryReactiveDiscoveryClient;
import com.yameizitd.gateway.spoiler.eventbus.EventListener;
import com.yameizitd.gateway.spoiler.eventbus.RefreshEvent;
import com.yameizitd.gateway.spoiler.eventbus.RouteRefreshEvent;
import com.yameizitd.gateway.spoiler.mapper.InstanceMapper;
import com.yameizitd.gateway.spoiler.mapper.RouteMapper;
import com.yameizitd.gateway.spoiler.mapper.ServiceMapper;
import com.yameizitd.gateway.spoiler.mapstruct.InstanceMapstruct;
import com.yameizitd.gateway.spoiler.mapstruct.RouteMapstruct;
import com.yameizitd.gateway.spoiler.route.RouteDefinitionChecker;
import com.yameizitd.gateway.spoiler.route.RouteDefinitionLoader;
import com.yameizitd.gateway.spoiler.route.RouteDefinitionManager;
import com.yameizitd.gateway.spoiler.route.impl.InMemoryRouteDefinitionLocator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class GatewayConfiguration {
    @Bean
    public SysDiscoveryClientLoader discoveryClientLoader(ServiceMapper serviceMapper,
                                                          InstanceMapper instanceMapper,
                                                          InstanceMapstruct instanceMapstruct) {
        return new SysDiscoveryClientLoader(serviceMapper, instanceMapper, instanceMapstruct);
    }

    @Bean
    public RouteDefinitionLoader routeDefinitionLoader(RouteMapper routeMapper, RouteMapstruct routeMapstruct) {
        return new RouteDefinitionLoader(routeMapper, routeMapstruct);
    }

    @Bean
    @ConditionalOnMissingBean(ServiceInstanceDefinitionManager.class)
    public InMemoryReactiveDiscoveryClient inMemoryReactiveDiscoveryClient(
            SysDiscoveryClientLoader discoveryClientLoader) {
        return new InMemoryReactiveDiscoveryClient(discoveryClientLoader);
    }

    @Configuration
    @ConditionalOnBean(InMemoryReactiveDiscoveryClient.class)
    public static class ServiceInstanceDefinitionManagerAssociatedConfiguration {
        @Bean
        public ServiceInstanceDefinitionManager serviceInstanceDefinitionManager(
                InMemoryReactiveDiscoveryClient reactiveDiscoveryClient) {
            return reactiveDiscoveryClient;
        }

        @Bean
        public ReactiveDiscoveryClient reactiveDiscoveryClient(InMemoryReactiveDiscoveryClient reactiveDiscoveryClient) {
            return reactiveDiscoveryClient;
        }

        @Bean
        public EventListener<RefreshEvent> eventListener(InMemoryReactiveDiscoveryClient reactiveDiscoveryClient) {
            return reactiveDiscoveryClient;
        }
    }

    @Bean
    @ConditionalOnMissingBean(RouteDefinitionManager.class)
    public InMemoryRouteDefinitionLocator inMemoryRouteDefinitionLocator(
            @Lazy RouteDefinitionChecker routeDefinitionChecker,
            RouteDefinitionLoader routeDefinitionLoader) {
        return new InMemoryRouteDefinitionLocator(routeDefinitionChecker, routeDefinitionLoader);
    }

    @Configuration
    @ConditionalOnBean(InMemoryRouteDefinitionLocator.class)
    public static class RouteDefinitionManagerAssociatedConfiguration {
        @Bean
        public RouteDefinitionManager routeDefinitionManager(InMemoryRouteDefinitionLocator routeDefinitionLocator) {
            return routeDefinitionLocator;
        }

        @Bean
        public RouteDefinitionLocator routeDefinitionLoader(InMemoryRouteDefinitionLocator routeDefinitionLocator) {
            return routeDefinitionLocator;
        }

        @Bean
        public EventListener<RouteRefreshEvent> eventListener(InMemoryRouteDefinitionLocator routeDefinitionLocator) {
            return routeDefinitionLocator;
        }
    }
}
