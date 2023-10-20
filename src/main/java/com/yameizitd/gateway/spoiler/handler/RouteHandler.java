package com.yameizitd.gateway.spoiler.handler;

import com.yameizitd.gateway.spoiler.domain.form.RouteCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.RouteQueryForm;
import com.yameizitd.gateway.spoiler.domain.form.RouteUpdateForm;
import com.yameizitd.gateway.spoiler.domain.view.RouteView;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import org.springframework.cloud.gateway.route.RouteDefinition;

public interface RouteHandler {
    int create(RouteCreateForm form);

    int remove(long id);

    int edit(RouteUpdateForm form);

    void batchEditByDefinition(RouteDefinition routeDefinition);

    int disable(long id);

    int enable(long id);

    IPage<RouteView> getPageableByOptions(RouteQueryForm query);
}
