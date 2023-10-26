package com.yameizitd.gateway.spoiler.handler.impl;

import com.yameizitd.gateway.spoiler.domain.entity.InstanceEntity;
import com.yameizitd.gateway.spoiler.domain.entity.ServiceEntity;
import com.yameizitd.gateway.spoiler.domain.form.InstanceCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.InstanceQueryForm;
import com.yameizitd.gateway.spoiler.domain.form.InstanceUpdateForm;
import com.yameizitd.gateway.spoiler.domain.view.InstanceView;
import com.yameizitd.gateway.spoiler.eventbus.RefreshEvent;
import com.yameizitd.gateway.spoiler.exception.impl.EntryNotExistException;
import com.yameizitd.gateway.spoiler.handler.EventHandler;
import com.yameizitd.gateway.spoiler.handler.InstanceHandler;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import com.yameizitd.gateway.spoiler.mapper.InstanceMapper;
import com.yameizitd.gateway.spoiler.mapper.ServiceMapper;
import com.yameizitd.gateway.spoiler.mapstruct.InstanceMapstruct;
import com.yameizitd.gateway.spoiler.util.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class InstanceHandlerImpl implements InstanceHandler {
    private final InstanceMapper instanceMapper;
    private final ServiceMapper serviceMapper;
    private final InstanceMapstruct instanceMapstruct;
    private final EventHandler eventHandler;

    public InstanceHandlerImpl(InstanceMapper instanceMapper,
                               ServiceMapper serviceMapper,
                               InstanceMapstruct instanceMapstruct,
                               EventHandler eventHandler) {
        this.instanceMapper = instanceMapper;
        this.serviceMapper = serviceMapper;
        this.instanceMapstruct = instanceMapstruct;
        this.eventHandler = eventHandler;
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
            eventHandler.publish(RefreshEvent.Operation.SAVE_INSTANCES, entity);
        }
        return inserted;
    }

    @Transactional
    @Override
    public int remove(long id) {
        InstanceEntity record = instanceMapper.selectById(id);
        int deleted = instanceMapper.delete(id);
        if (deleted > 0) {
            eventHandler.publish(RefreshEvent.Operation.DELETE_INSTANCES, record);
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
            eventHandler.publish(RefreshEvent.Operation.DELETE_INSTANCES, record);
        }
        return disabled;
    }

    @Transactional
    @Override
    public int enable(long id) {
        InstanceEntity record = instanceMapper.selectById(id);
        int enabled = instanceMapper.enable(id);
        if (enabled > 0) {
            eventHandler.publish(RefreshEvent.Operation.SAVE_INSTANCES, record);
        }
        return enabled;
    }

    @Transactional
    @Override
    public int enableByServiceId(long serviceId) {
        List<InstanceEntity> records = instanceMapper.selectEnabledByServiceId(serviceId);
        if (records != null && !records.isEmpty()) {
            eventHandler.publish(RefreshEvent.Operation.SAVE_INSTANCES, records);
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
            eventHandler.publish(RefreshEvent.Operation.SAVE_INSTANCES, entity);
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
}
