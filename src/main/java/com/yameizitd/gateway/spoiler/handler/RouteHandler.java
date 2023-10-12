package com.yameizitd.gateway.spoiler.handler;

import com.yameizitd.gateway.spoiler.domain.form.RouteCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.RouteQueryForm;
import com.yameizitd.gateway.spoiler.domain.form.RouteUpdateForm;
import com.yameizitd.gateway.spoiler.domain.form.RouteView;
import com.yameizitd.gateway.spoiler.interceptor.IPage;

public interface RouteHandler {
    int create(RouteCreateForm form);

    int remove(long id);

    int edit(RouteUpdateForm form);

    int disable(long id);

    int enable(long id);

    IPage<RouteView> getPageableByOptions(RouteQueryForm form);
}
