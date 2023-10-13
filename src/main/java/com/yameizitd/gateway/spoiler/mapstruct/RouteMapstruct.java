package com.yameizitd.gateway.spoiler.mapstruct;

import com.fasterxml.jackson.databind.JsonNode;
import com.yameizitd.gateway.spoiler.domain.entity.RichRouteEntity;
import com.yameizitd.gateway.spoiler.domain.entity.RouteEntity;
import com.yameizitd.gateway.spoiler.domain.form.RouteCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.RouteUpdateForm;
import com.yameizitd.gateway.spoiler.domain.form.RouteView;
import com.yameizitd.gateway.spoiler.util.IdUtils;
import com.yameizitd.gateway.spoiler.util.JacksonUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Mapper(componentModel = "spring", uses = JacksonUtils.class)
public interface RouteMapstruct {
    String LB_PREFIX = "lb://";

    default RouteEntity createForm2entity(RouteCreateForm form) {
        RouteEntity entity = createForm2entity0(form);
        entity.setId(IdUtils.nextSnowflakeId());
        if (entity.getTemplateId() == null) {
            entity.setTemplateId(-1L);
        }
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        entity.setEnabled(true);
        return entity;
    }

    @Mapping(source = "predicates", target = "predicates", qualifiedByName = "jsonNode2string")
    @Mapping(source = "filters", target = "filters", qualifiedByName = "jsonNode2string")
    @Mapping(source = "metadata", target = "metadata", qualifiedByName = "jsonNode2string")
    RouteEntity createForm2entity0(RouteCreateForm form);

    default RouteEntity updateForm2entity(RouteUpdateForm form) {
        RouteEntity entity = updateForm2entity0(form);
        if (entity.getTemplateId() == null) {
            entity.setTemplateId(-1L);
        }
        entity.setUpdateTime(LocalDateTime.now());
        return entity;
    }

    @Mapping(source = "predicates", target = "predicates", qualifiedByName = "jsonNode2string")
    @Mapping(source = "filters", target = "filters", qualifiedByName = "jsonNode2string")
    @Mapping(source = "metadata", target = "metadata", qualifiedByName = "jsonNode2string")
    RouteEntity updateForm2entity0(RouteUpdateForm form);

    @Mapping(source = "predicates", target = "predicates", qualifiedByName = "string2jsonNode")
    @Mapping(source = "filters", target = "filters", qualifiedByName = "string2jsonNode")
    @Mapping(source = "metadata", target = "metadata", qualifiedByName = "string2jsonNode")
    RouteView richEntity2view(RichRouteEntity entity);

    default RouteDefinition entity2definition(RouteEntity entity) {
        RouteDefinition definition = new RouteDefinition();
        definition.setId(String.valueOf(entity.getId()));
        JsonNode predicatesArray = JacksonUtils.string2jsonNode(entity.getPredicates());
        if (predicatesArray != null && predicatesArray.isArray()) {
            List predicates = JacksonUtils.jsonNode2list(predicatesArray);
            List<PredicateDefinition> predicateDefinitions = predicates.stream()
                    .map(predicate -> JacksonUtils.map2obj((Map) predicate, PredicateDefinition.class))
                    .toList();
            definition.setPredicates(predicateDefinitions);
        }
        JsonNode filtersArray = JacksonUtils.string2jsonNode(entity.getFilters());
        if (filtersArray != null && filtersArray.isArray()) {
            List filters = JacksonUtils.jsonNode2list(filtersArray);
            List<FilterDefinition> filterDefinitions = filters.stream()
                    .map(filter -> JacksonUtils.map2obj((Map) filter, FilterDefinition.class))
                    .toList();
            definition.setFilters(filterDefinitions);
        }
        definition.setUri(URI.create(LB_PREFIX + entity.getServiceId()));
        Map metadata = JacksonUtils.string2map(entity.getMetadata());
        definition.setMetadata(metadata);
        definition.setOrder(entity.getOrdered() != null ? entity.getOrdered() : 0);
        return definition;
    }
}
