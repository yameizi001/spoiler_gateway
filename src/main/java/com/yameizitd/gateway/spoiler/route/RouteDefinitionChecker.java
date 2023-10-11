package com.yameizitd.gateway.spoiler.route;

import com.yameizitd.gateway.spoiler.exception.impl.RouteInvalidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRouteLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
public class RouteDefinitionChecker implements ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext;
    private RouteDefinitionRouteLocator routeDefinitionRouteLocator;
    private Method method;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.routeDefinitionRouteLocator = (RouteDefinitionRouteLocator) applicationContext
                .getBean("routeDefinitionRouteLocator", RouteLocator.class);
        Method[] methods = this.routeDefinitionRouteLocator.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if ("convertToRoute".equals(method.getName())) {
                method.setAccessible(true);
                this.method = method;
            }
        }
    }

    public boolean check(RouteDefinition routeDefinition) {
        try {
            method.invoke(routeDefinitionRouteLocator, routeDefinition);
            return true;
        } catch (Exception e) {
            log.error("Route definition not valid", e);
            return false;
        }
    }

    public Route convert(RouteDefinition routeDefinition) throws RouteInvalidException {
        try {
            return (Route) method.invoke(routeDefinitionRouteLocator, routeDefinition);
        } catch (Exception e) {
            log.error("Route definition not valid", e);
            throw new RouteInvalidException("Route definition not valid", e);
        }
    }
}
