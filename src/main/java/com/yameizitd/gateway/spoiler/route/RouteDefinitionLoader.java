package com.yameizitd.gateway.spoiler.route;

import com.yameizitd.gateway.spoiler.domain.entity.RichRouteEntity;
import com.yameizitd.gateway.spoiler.domain.form.RouteQueryForm;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import com.yameizitd.gateway.spoiler.mapper.RouteMapper;
import com.yameizitd.gateway.spoiler.mapstruct.RouteMapstruct;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RouteDefinitionLoader {
    private static final long START_PAGE_NUM = 1;
    private static final long PAGE_SIZE = 50;
    private final Map<String, RouteDefinition> cache = new HashMap<>(256);
    private final RouteMapper routeMapper;
    private final RouteMapstruct routeMapstruct;

    public RouteDefinitionLoader(RouteMapper routeMapper, RouteMapstruct routeMapstruct) {
        this.routeMapper = routeMapper;
        this.routeMapstruct = routeMapstruct;
    }

    public Map<String, RouteDefinition> loadedRoutes() {
        return cache;
    }

    public Map<String, RouteDefinition> refresh() {
        cache.clear();
        load(START_PAGE_NUM);
        return cache;
    }

    private void load(long num) {
        List<RichRouteEntity> routeRecords = routeMapper.selectByOptions(
                new RouteQueryForm(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        true,
                        null
                ),
                IPage.of(num, PAGE_SIZE)
        );
        if (routeRecords == null || routeRecords.isEmpty()) {
            return;
        }
        Map<String, RouteDefinition> routeDefinitionMap = routeRecords.stream()
                .map(routeMapstruct::richEntity2entity)
                .map(routeMapstruct::entity2definition)
                .collect(Collectors.toMap(RouteDefinition::getId, routeDefinition -> routeDefinition));
        cache.putAll(routeDefinitionMap);
        load(++num);
    }
}
