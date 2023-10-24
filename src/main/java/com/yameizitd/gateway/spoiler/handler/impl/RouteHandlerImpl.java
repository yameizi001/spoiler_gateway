package com.yameizitd.gateway.spoiler.handler.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.yameizitd.gateway.spoiler.domain.TemplateType;
import com.yameizitd.gateway.spoiler.domain.entity.RichRouteEntity;
import com.yameizitd.gateway.spoiler.domain.entity.RouteEntity;
import com.yameizitd.gateway.spoiler.domain.entity.ServiceEntity;
import com.yameizitd.gateway.spoiler.domain.form.*;
import com.yameizitd.gateway.spoiler.domain.view.RouteView;
import com.yameizitd.gateway.spoiler.eventbus.EventPublisher;
import com.yameizitd.gateway.spoiler.eventbus.RefreshEvent;
import com.yameizitd.gateway.spoiler.eventbus.RouteRefreshEvent;
import com.yameizitd.gateway.spoiler.exception.impl.EntryNotExistException;
import com.yameizitd.gateway.spoiler.handler.RouteHandler;
import com.yameizitd.gateway.spoiler.handler.TemplateHandler;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import com.yameizitd.gateway.spoiler.mapper.RouteMapper;
import com.yameizitd.gateway.spoiler.mapper.ServiceMapper;
import com.yameizitd.gateway.spoiler.mapstruct.RouteMapstruct;
import com.yameizitd.gateway.spoiler.util.JacksonUtils;
import com.yameizitd.gateway.spoiler.util.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RouteHandlerImpl implements RouteHandler {
    private final RouteMapper routeMapper;
    private final ServiceMapper serviceMapper;
    private final RouteMapstruct routeMapstruct;
    private final EventPublisher eventPublisher;
    private final TemplateHandler templateHandler;

    public RouteHandlerImpl(RouteMapper routeMapper,
                            ServiceMapper serviceMapper,
                            RouteMapstruct routeMapstruct,
                            EventPublisher eventPublisher,
                            TemplateHandler templateHandler) {
        this.routeMapper = routeMapper;
        this.serviceMapper = serviceMapper;
        this.routeMapstruct = routeMapstruct;
        this.eventPublisher = eventPublisher;
        this.templateHandler = templateHandler;
    }

    @Transactional
    @Override
    public int create(RouteCreateForm form) {
        ((RouteHandlerImpl) AopContext.currentProxy()).checkRouteAssociatedEntity(form.getServiceId());
        RouteEntity entity = routeMapstruct.createForm2entity(form);
        int inserted = routeMapper.insert(entity);
        if (inserted > 0) {
            ((RouteHandlerImpl) AopContext.currentProxy()).publish(RefreshEvent.Operation.SAVE_ROUTES, entity);
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
    public int createFromTemplate(RouteWithTemplateUpsertForm form) {
        ((RouteHandlerImpl) AopContext.currentProxy()).checkRouteAssociatedEntity(form.getServiceId());
        TemplateUpsertForm templateForm = form.getTemplate();
        templateForm.setName(LocalDateTime.now().toString());
        templateForm.setType(TemplateType.INSTANT);
        // create template
        RouteDefinition routeDefinition = templateHandler.create(templateForm);
        String templateId = routeDefinition.getId();
        JsonNode predicates = JacksonUtils.list2jsonNode(routeDefinition.getPredicates());
        JsonNode filters = JacksonUtils.list2jsonNode(routeDefinition.getFilters());
        JsonNode metadata = JacksonUtils.map2jsonNode(routeDefinition.getMetadata());
        // build route create form
        RouteCreateForm routeForm = new RouteCreateForm(
                form.getServiceId(),
                Long.valueOf(templateId),
                form.getName(),
                form.getDescription(),
                predicates,
                filters,
                form.getOrdered(),
                metadata
        );
        // create route
        return create(routeForm);
    }

    @Transactional
    @Override
    public int remove(long id) {
        RouteEntity record = routeMapper.selectById(id);
        if (record == null) {
            throw new EntryNotExistException("Route not exist");
        }
        int deleted = routeMapper.delete(id);
        if (deleted > 0) {
            // remove associated instant template
            templateHandler.removeByIdAndType(record.getTemplateId(), TemplateType.INSTANT);
            ((RouteHandlerImpl) AopContext.currentProxy()).publish(RefreshEvent.Operation.DELETE_ROUTES, record);
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
            ((RouteHandlerImpl) AopContext.currentProxy()).publish(RefreshEvent.Operation.SAVE_ROUTES, record);
        }
        return updated;
    }

    @Transactional
    @Override
    public int editFromTemplate(RouteWithTemplateUpsertForm form) {
        // check service
        Long serviceId = form.getServiceId();
        checkRouteAssociatedEntity(serviceId);
        // check route
        Long routeId = form.getId();
        RouteEntity record = routeMapper.selectById(routeId);
        if (record == null) {
            throw new EntryNotExistException("Route not exit");
        }
        TemplateUpsertForm templateForm = form.getTemplate();
        templateForm.setName(LocalDateTime.now().toString());
        templateForm.setType(TemplateType.INSTANT);
        // edit template
        RouteDefinition routeDefinition = templateHandler.edit(templateForm);
        String templateId = routeDefinition.getId();
        JsonNode predicates = JacksonUtils.list2jsonNode(routeDefinition.getPredicates());
        JsonNode filters = JacksonUtils.list2jsonNode(routeDefinition.getFilters());
        JsonNode metadata = JacksonUtils.map2jsonNode(routeDefinition.getMetadata());
        // build route update form
        RouteUpdateForm routeForm = new RouteUpdateForm(
                routeId,
                serviceId,
                Long.valueOf(templateId),
                form.getName(),
                form.getDescription(),
                predicates,
                filters,
                form.getOrdered(),
                metadata
        );
        // edit route
        return edit(routeForm);
    }

    @Transactional
    @Override
    public int disable(long id) {
        int disabled = routeMapper.disable(id);
        if (disabled > 0) {
            RouteEntity entity = new RouteEntity();
            entity.setId(id);
            ((RouteHandlerImpl) AopContext.currentProxy()).publish(RefreshEvent.Operation.DELETE_ROUTES, entity);
        }
        return disabled;
    }

    @Transactional
    @Override
    public int enable(long id) {
        int enabled = routeMapper.enable(id);
        if (enabled > 0) {
            RouteEntity record = routeMapper.selectById(id);
            ((RouteHandlerImpl) AopContext.currentProxy()).publish(RefreshEvent.Operation.SAVE_ROUTES, record);
        }
        return enabled;
    }

    @Override
    public IPage<RouteView> getPageableByOptions(RouteQueryForm query) {
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
