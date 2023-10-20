package com.yameizitd.gateway.spoiler.domain.business;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.*;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RichRouteDefinition implements Serializable {
    @Serial
    private static final long serialVersionUID = -7453571058199880056L;

    private List<ElementWithPropertyValues> predicates = new ArrayList<>();
    private List<ElementWithPropertyValues> filters = new ArrayList<>();
    private List<PropertyValues> metadata = new ArrayList<>();

    public void populateRouteDefinition(RouteDefinition routeDefinition) {
        for (ElementWithPropertyValues predicate : predicates) {
            PredicateDefinition predicateDefinition = new PredicateDefinition();
            String name = predicate.getName();
            predicateDefinition.setName(name);
            populateArgs(predicateDefinition.getArgs(), predicate.getPropertyValues());
            routeDefinition.getPredicates().add(predicateDefinition);
        }
        sortFilters();
        for (ElementWithPropertyValues filter : filters) {
            FilterDefinition filterDefinition = new FilterDefinition();
            String name = filter.getName();
            filterDefinition.setName(name);
            populateArgs(filterDefinition.getArgs(), filter.getPropertyValues());
            routeDefinition.getFilters().add(filterDefinition);
        }
        Map<String, Object> routeMetadata = metadata.stream()
                .filter(propertyValues -> StringUtils.hasLength(propertyValues.getKey()))
                .collect(Collectors.toMap(PropertyValues::getKey, propertyValue -> {
                    JsonNode values = propertyValue.getValues();
                    ArrayNode array = (ArrayNode) values;
                    return array.get(0).asText();
                }));
        routeDefinition.setMetadata(routeMetadata);
    }

    private void populateArgs(Map<String, String> args, List<PropertyValues> propertyValuesList) {
        int i = 0;
        for (PropertyValues propertyValues : propertyValuesList) {
            String key = propertyValues.getKey();
            ArrayNode array = (ArrayNode) propertyValues.getValues();
            if (key == null) {
                for (JsonNode node : array) {
                    args.put(NameUtils.generateName(i++), node.asText());
                }
            } else {
                String val = array.get(0).asText();
                args.put(key, val);
            }
        }
    }

    private void sortFilters() {
        List<ElementWithPropertyValues> orderedFilters = new ArrayList<>();
        int i = 0;
        for (ElementWithPropertyValues filter : filters) {
            if (filter.getOrdered() == null) {
                filter.setOrdered(i++);
            }
            orderedFilters.add(filter);
        }
        orderedFilters.sort(Comparator.comparingInt(ElementWithPropertyValues::getOrdered));
        this.filters = orderedFilters;
    }
}
