package com.yameizitd.gateway.spoiler.handler.impl;

import com.yameizitd.gateway.spoiler.domain.form.RouteCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.RouteQueryForm;
import com.yameizitd.gateway.spoiler.domain.form.RouteUpdateForm;
import com.yameizitd.gateway.spoiler.domain.form.RouteView;
import com.yameizitd.gateway.spoiler.eventbus.EventPublisher;
import com.yameizitd.gateway.spoiler.handler.RouteHandler;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import com.yameizitd.gateway.spoiler.mapper.RouteMapper;
import com.yameizitd.gateway.spoiler.mapstruct.RouteMapstruct;
import org.springframework.stereotype.Service;

@Service
public class RouterHandlerImpl implements RouteHandler {
    private final RouteMapper routeMapper;
    private final RouteMapstruct routeMapstruct;
    private final EventPublisher eventPublisher;

    public RouterHandlerImpl(RouteMapper routeMapper,
                             RouteMapstruct routeMapstruct,
                             EventPublisher eventPublisher) {
        this.routeMapper = routeMapper;
        this.routeMapstruct = routeMapstruct;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public int create(RouteCreateForm form) {
        return 0;
    }

    @Override
    public int remove(long id) {
        return 0;
    }

    @Override
    public int edit(RouteUpdateForm form) {
        return 0;
    }

    @Override
    public int disable(long id) {
        return 0;
    }

    @Override
    public int enable(long id) {
        return 0;
    }

    @Override
    public IPage<RouteView> getPageableByOptions(RouteQueryForm form) {
        return null;
    }
}
