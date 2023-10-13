package com.yameizitd.gateway.spoiler.handler.impl;

import com.yameizitd.gateway.spoiler.domain.entity.RichRouteEntity;
import com.yameizitd.gateway.spoiler.domain.entity.RouteEntity;
import com.yameizitd.gateway.spoiler.domain.entity.ServiceEntity;
import com.yameizitd.gateway.spoiler.domain.form.RouteCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.RouteQueryForm;
import com.yameizitd.gateway.spoiler.domain.form.RouteUpdateForm;
import com.yameizitd.gateway.spoiler.domain.form.RouteView;
import com.yameizitd.gateway.spoiler.eventbus.EventPublisher;
import com.yameizitd.gateway.spoiler.eventbus.RefreshEvent;
import com.yameizitd.gateway.spoiler.eventbus.RouteRefreshEvent;
import com.yameizitd.gateway.spoiler.exception.impl.EntryNotExistException;
import com.yameizitd.gateway.spoiler.handler.RouteHandler;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import com.yameizitd.gateway.spoiler.mapper.RouteMapper;
import com.yameizitd.gateway.spoiler.mapper.ServiceMapper;
import com.yameizitd.gateway.spoiler.mapstruct.RouteMapstruct;
import com.yameizitd.gateway.spoiler.util.PageUtils;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RouterHandlerImpl implements RouteHandler {
    private final RouteMapper routeMapper;
    private final ServiceMapper serviceMapper;
    private final RouteMapstruct routeMapstruct;
    private final EventPublisher eventPublisher;

    public RouterHandlerImpl(RouteMapper routeMapper,
                             ServiceMapper serviceMapper,
                             RouteMapstruct routeMapstruct,
                             EventPublisher eventPublisher) {
        this.routeMapper = routeMapper;
        this.serviceMapper = serviceMapper;
        this.routeMapstruct = routeMapstruct;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    @Override
    public int create(RouteCreateForm form) {
        checkRouteAssociatedEntity(form.getServiceId());
        RouteEntity entity = routeMapstruct.createForm2entity(form);
        int inserted = routeMapper.insert(entity);
        if (inserted > 0) {
            publish(RefreshEvent.Operation.SAVE_ROUTES, entity);
        }
        return inserted;
    }

    private void checkRouteAssociatedEntity(Long serviceId) {
        ServiceEntity serviceRecord = serviceMapper.selectByIdForUpdate(serviceId);
        if (serviceRecord == null) {
            throw new EntryNotExistException("Service not exist");
        }
    }

    @Transactional
    @Override
    public int remove(long id) {
        int deleted = routeMapper.delete(id);
        if (deleted > 0) {
            RouteEntity entity = new RouteEntity();
            entity.setId(id);
            publish(RefreshEvent.Operation.DELETE_ROUTES, entity);
        }
        return deleted;
    }

    @Transactional
    @Override
    public int edit(RouteUpdateForm form) {
        checkRouteAssociatedEntity(form.getServiceId());
        RouteEntity entity = routeMapstruct.updateForm2entity(form);
        int updated = routeMapper.update(entity);
        if (updated > 0) {
            RouteEntity record = routeMapper.selectById(form.getId());
            publish(RefreshEvent.Operation.SAVE_ROUTES, record);
        }
        return updated;
    }

    @Transactional
    @Override
    public int disable(long id) {
        int disabled = routeMapper.disable(id);
        if (disabled > 0) {
            RouteEntity entity = new RouteEntity();
            entity.setId(id);
            publish(RefreshEvent.Operation.DELETE_ROUTES, entity);
        }
        return disabled;
    }

    @Transactional
    @Override
    public int enable(long id) {
        int enabled = routeMapper.enable(id);
        if (enabled > 0) {
            RouteEntity record = routeMapper.selectById(id);
            publish(RefreshEvent.Operation.SAVE_ROUTES, record);
        }
        return enabled;
    }

    @Override
    public IPage<RouteView> getPageableByOptions(RouteQueryForm query) {
        // todo: populate template properties
        IPage<RichRouteEntity> page = query.getPage().iPage();
        List<RichRouteEntity> records = routeMapper.selectByOptions(query, page);
        page.setRecords(records);
        return PageUtils.map(page, routeMapstruct::richEntity2view);
    }

    private void publish(RefreshEvent.Operation operation, RouteEntity entity) {
        if (entity != null) {
            publish(operation, List.of(entity));
        }
    }

    private void publish(RefreshEvent.Operation operation, List<RouteEntity> entities) {
        if (entities != null && !entities.isEmpty()) {
            List<RouteDefinition> definitions = entities.stream().map(routeMapstruct::entity2definition).toList();
            RouteRefreshEvent event = new RouteRefreshEvent(operation, definitions);
            eventPublisher.publish(Mono.just(event)).block(Duration.of(2L, TimeUnit.SECONDS.toChronoUnit()));
        }
    }
}
