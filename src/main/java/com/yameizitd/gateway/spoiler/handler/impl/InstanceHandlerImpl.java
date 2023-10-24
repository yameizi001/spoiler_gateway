package com.yameizitd.gateway.spoiler.handler.impl;

import com.yameizitd.gateway.spoiler.domain.entity.InstanceEntity;
import com.yameizitd.gateway.spoiler.domain.entity.ServiceEntity;
import com.yameizitd.gateway.spoiler.domain.form.InstanceCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.InstanceQueryForm;
import com.yameizitd.gateway.spoiler.domain.form.InstanceUpdateForm;
import com.yameizitd.gateway.spoiler.domain.view.InstanceView;
import com.yameizitd.gateway.spoiler.eventbus.EventPublisher;
import com.yameizitd.gateway.spoiler.eventbus.InstanceRefreshEvent;
import com.yameizitd.gateway.spoiler.eventbus.RefreshEvent;
import com.yameizitd.gateway.spoiler.exception.impl.EntryNotExistException;
import com.yameizitd.gateway.spoiler.handler.InstanceHandler;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import com.yameizitd.gateway.spoiler.mapper.InstanceMapper;
import com.yameizitd.gateway.spoiler.mapper.ServiceMapper;
import com.yameizitd.gateway.spoiler.mapstruct.InstanceMapstruct;
import com.yameizitd.gateway.spoiler.util.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class InstanceHandlerImpl implements InstanceHandler {
    private final InstanceMapper instanceMapper;
    private final InstanceMapstruct instanceMapstruct;
    private final EventPublisher eventPublisher;
    private final ServiceMapper serviceMapper;

    public InstanceHandlerImpl(InstanceMapper instanceMapper, InstanceMapstruct instanceMapstruct,
                               EventPublisher eventPublisher, ServiceMapper serviceMapper) {
        this.instanceMapper = instanceMapper;
        this.instanceMapstruct = instanceMapstruct;
        this.eventPublisher = eventPublisher;
        this.serviceMapper = serviceMapper;
    }

    @Transactional
    @Override
    public int create(InstanceCreateForm form) {
        ServiceEntity service = serviceMapper.selectById(form.getServiceId());
        if (service == null) {
            throw new EntryNotExistException("Service not exist");
        }
        InstanceEntity entity = instanceMapstruct.createForm2entity(form);
        int inserted = instanceMapper.insert(entity);
        if (inserted > 0) {
            ((InstanceHandlerImpl) AopContext.currentProxy()).publish(RefreshEvent.Operation.SAVE_INSTANCES, entity);
        }
        return inserted;
    }

    @Transactional
    @Override
    public int remove(long id) {
        InstanceEntity record = instanceMapper.selectById(id);
        int deleted = instanceMapper.delete(id);
        if (deleted > 0) {
            ((InstanceHandlerImpl) AopContext.currentProxy()).publish(RefreshEvent.Operation.DELETE_INSTANCES, record);
        }
        return deleted;
    }

    @Transactional
    @Override
    public int removeByServiceId(long serviceId) {
        return instanceMapper.deleteByServiceId(serviceId);
    }

    @Transactional
    @Override
    public int disable(long id) {
        InstanceEntity record = instanceMapper.selectById(id);
        int disabled = instanceMapper.disable(id);
        if (disabled > 0) {
            ((InstanceHandlerImpl) AopContext.currentProxy()).publish(RefreshEvent.Operation.DELETE_INSTANCES, record);
        }
        return disabled;
    }

    @Transactional
    @Override
    public int enable(long id) {
        InstanceEntity record = instanceMapper.selectById(id);
        int enabled = instanceMapper.enable(id);
        if (enabled > 0) {
            ((InstanceHandlerImpl) AopContext.currentProxy()).publish(RefreshEvent.Operation.SAVE_INSTANCES, record);
        }
        return enabled;
    }

    @Transactional
    @Override
    public int enableByServiceId(long serviceId) {
        List<InstanceEntity> records = instanceMapper.selectEnabledByServiceId(serviceId);
        if (records != null && !records.isEmpty()) {
            ((InstanceHandlerImpl) AopContext.currentProxy()).publish(RefreshEvent.Operation.SAVE_INSTANCES, records);
            return records.size();
        }
        return 0;
    }

    @Transactional
    @Override
    public int edit(InstanceUpdateForm form) {
        InstanceEntity entity = instanceMapstruct.updateForm2entity(form);
        int updated = instanceMapper.update(entity);
        if (updated > 0) {
            ((InstanceHandlerImpl) AopContext.currentProxy()).publish(RefreshEvent.Operation.SAVE_INSTANCES, entity);
        }
        return updated;
    }

    @Override
    public IPage<InstanceView> getPageableByOptions(InstanceQueryForm query) {
        IPage<InstanceEntity> page = query.getPage().iPage();
        List<InstanceEntity> records = instanceMapper.selectByOptions(query, page);
        page.setRecords(records);
        return PageUtils.map(page, instanceMapstruct::entity2view);
    }

    private void publish(RefreshEvent.Operation operation, InstanceEntity entity) {
        if (entity != null) {
            publish(operation, List.of(entity));
        }
    }

    private void publish(RefreshEvent.Operation operation, List<InstanceEntity> entities) {
        if (entities != null && !entities.isEmpty()) {
            List<ServiceInstance> instances = entities.stream().map(instanceMapstruct::entity2definition).toList();
            InstanceRefreshEvent event = new InstanceRefreshEvent(operation, instances);
            eventPublisher.publish(Mono.just(event)).block(Duration.of(2L, TimeUnit.SECONDS.toChronoUnit()));
        }
    }
}
