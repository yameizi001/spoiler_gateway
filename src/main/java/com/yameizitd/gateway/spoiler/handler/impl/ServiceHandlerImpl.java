package com.yameizitd.gateway.spoiler.handler.impl;

import com.yameizitd.gateway.spoiler.domain.business.ServiceDefinition;
import com.yameizitd.gateway.spoiler.domain.entity.ServiceEntity;
import com.yameizitd.gateway.spoiler.domain.form.ServiceCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.ServiceQueryForm;
import com.yameizitd.gateway.spoiler.domain.form.ServiceUpdateForm;
import com.yameizitd.gateway.spoiler.domain.view.ServiceView;
import com.yameizitd.gateway.spoiler.eventbus.EventPublisher;
import com.yameizitd.gateway.spoiler.eventbus.RefreshEvent;
import com.yameizitd.gateway.spoiler.eventbus.ServiceRefreshEvent;
import com.yameizitd.gateway.spoiler.exception.impl.EntryInuseException;
import com.yameizitd.gateway.spoiler.handler.InstanceHandler;
import com.yameizitd.gateway.spoiler.handler.ServiceHandler;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import com.yameizitd.gateway.spoiler.mapper.RouteMapper;
import com.yameizitd.gateway.spoiler.mapper.ServiceMapper;
import com.yameizitd.gateway.spoiler.mapstruct.ServiceMapstruct;
import com.yameizitd.gateway.spoiler.util.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ServiceHandlerImpl implements ServiceHandler {
    private final ServiceMapper serviceMapper;
    private final RouteMapper routeMapper;
    private final ServiceMapstruct serviceMapstruct;
    private final EventPublisher eventPublisher;
    private final InstanceHandler instanceHandler;

    public ServiceHandlerImpl(ServiceMapper serviceMapper,
                              RouteMapper routeMapper,
                              ServiceMapstruct serviceMapstruct,
                              EventPublisher eventPublisher,
                              InstanceHandler instanceHandler) {
        this.serviceMapper = serviceMapper;
        this.routeMapper = routeMapper;
        this.serviceMapstruct = serviceMapstruct;
        this.eventPublisher = eventPublisher;
        this.instanceHandler = instanceHandler;
    }

    @Transactional
    @Override
    public int create(ServiceCreateForm form) {
        ServiceEntity entity = serviceMapstruct.createForm2entity(form);
        int inserted = serviceMapper.insert(entity);
        if (inserted > 0) {
            ((ServiceHandlerImpl) AopContext.currentProxy()).publish(RefreshEvent.Operation.SAVE_SERVICES, entity);
        }
        return inserted;
    }

    @Transactional
    @Override
    public int remove(long id) {
        // check service is inuse
        boolean inuse = routeMapper.existByServiceId(id);
        if (inuse) {
            throw new EntryInuseException("Service is inuse");
        }
        int deleted = serviceMapper.delete(id);
        if (deleted > 0) {
            // delete all associated instances
            int removedInstances = instanceHandler.removeByServiceId(id);
            ServiceEntity entity = new ServiceEntity();
            entity.setId(id);
            ((ServiceHandlerImpl) AopContext.currentProxy()).publish(RefreshEvent.Operation.DELETE_SERVICES, entity);
        }
        return deleted;
    }

    @Transactional
    @Override
    public int edit(ServiceUpdateForm form) {
        ServiceEntity entity = serviceMapstruct.updateForm2entity(form);
        int updated = serviceMapper.update(entity);
        if (updated > 0) {
            ((ServiceHandlerImpl) AopContext.currentProxy()).publish(RefreshEvent.Operation.DELETE_SERVICES, entity);
            ((ServiceHandlerImpl) AopContext.currentProxy()).publish(RefreshEvent.Operation.SAVE_SERVICES, entity);
        }
        return updated;
    }

    @Transactional
    @Override
    public int disable(long id) {
        int disabled = serviceMapper.disable(id);
        if (disabled > 0) {
            ServiceEntity entity = new ServiceEntity();
            entity.setId(id);
            ((ServiceHandlerImpl) AopContext.currentProxy()).publish(RefreshEvent.Operation.DELETE_SERVICES, entity);
        }
        return disabled;
    }

    @Transactional
    @Override
    public int enable(long id) {
        int enabled = serviceMapper.enable(id);
        if (enabled > 0) {
            ((ServiceHandlerImpl) AopContext.currentProxy()).publish(
                    RefreshEvent.Operation.SAVE_SERVICES,
                    serviceMapper.selectById(id)
            );
            int enabledInstances = instanceHandler.enableByServiceId(id);
        }
        return enabled;
    }

    @Override
    public IPage<ServiceView> getPageableByOptions(ServiceQueryForm query) {
        IPage<ServiceEntity> page = query.getPage().iPage();
        List<ServiceEntity> records = serviceMapper.selectByOptions(query, page);
        page.setRecords(records);
        return PageUtils.map(page, serviceMapstruct::entity2view);
    }

    private void publish(RefreshEvent.Operation operation, ServiceEntity entity) {
        if (entity != null) {
            publish(operation, List.of(entity));
        }
    }

    private void publish(RefreshEvent.Operation operation, List<ServiceEntity> entities) {
        if (entities != null && !entities.isEmpty()) {
            List<ServiceDefinition> definitions = entities.stream().map(serviceMapstruct::entity2definition).toList();
            ServiceRefreshEvent event = new ServiceRefreshEvent(operation, definitions);
            eventPublisher.publish(Mono.just(event)).block(Duration.of(2L, TimeUnit.SECONDS.toChronoUnit()));
        }
    }
}
