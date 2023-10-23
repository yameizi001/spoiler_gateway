package com.yameizitd.gateway.spoiler.handler;

import com.yameizitd.gateway.spoiler.domain.form.TemplateQueryForm;
import com.yameizitd.gateway.spoiler.domain.form.TemplateUpsertForm;
import com.yameizitd.gateway.spoiler.domain.view.PropertyValuesView;
import com.yameizitd.gateway.spoiler.domain.view.SimpleTemplateView;
import com.yameizitd.gateway.spoiler.domain.view.TemplateDetailView;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TemplateHandler {
    String TEMPORARY_ROUTE_DEFINITION_ID = "__spoiler_gateway_embed_unreachable_route";
    String TEMPORARY_ROUTE_DEFINITION_URI = "lb://__spoiler_gateway_embed_unreachable_service";

    RouteDefinition checkAndApply(TemplateUpsertForm form);

    RouteDefinition create(TemplateUpsertForm form);

    default RouteDefinition temporary() {
        RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.setId(TEMPORARY_ROUTE_DEFINITION_ID);
        routeDefinition.setPredicates(new ArrayList<>());
        routeDefinition.setFilters(new ArrayList<>());
        routeDefinition.setUri(URI.create(TEMPORARY_ROUTE_DEFINITION_URI));
        routeDefinition.setMetadata(new HashMap<>());
        routeDefinition.setOrder(0);
        return routeDefinition;
    }

    int remove(long id);

    RouteDefinition edit(TemplateUpsertForm form);

    IPage<SimpleTemplateView> getPageableByOptions(TemplateQueryForm query);

    TemplateDetailView getDetailById(long id);

    List<PropertyValuesView> getPropertiesByTemplateIdAndElementId(long templateId, long elementId);
}
