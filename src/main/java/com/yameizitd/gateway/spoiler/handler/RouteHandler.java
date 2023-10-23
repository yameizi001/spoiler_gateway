package com.yameizitd.gateway.spoiler.handler;

import com.yameizitd.gateway.spoiler.domain.form.RouteCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.RouteQueryForm;
import com.yameizitd.gateway.spoiler.domain.form.RouteUpdateForm;
import com.yameizitd.gateway.spoiler.domain.form.RouteWithTemplateUpsertForm;
import com.yameizitd.gateway.spoiler.domain.view.RouteView;
import com.yameizitd.gateway.spoiler.interceptor.IPage;

public interface RouteHandler {
    int create(RouteCreateForm form);

    int createFromTemplate(RouteWithTemplateUpsertForm form);

    int remove(long id);

    int edit(RouteUpdateForm form);

    int editFromTemplate(RouteWithTemplateUpsertForm form);

    int disable(long id);

    int enable(long id);

    IPage<RouteView> getPageableByOptions(RouteQueryForm query);
}
