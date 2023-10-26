package com.yameizitd.gateway.spoiler.handler.impl;

import com.yameizitd.gateway.spoiler.domain.entity.ServiceEntity;
import com.yameizitd.gateway.spoiler.domain.form.ServiceCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.ServiceQueryForm;
import com.yameizitd.gateway.spoiler.domain.form.ServiceUpdateForm;
import com.yameizitd.gateway.spoiler.domain.view.ServiceView;
import com.yameizitd.gateway.spoiler.eventbus.RefreshEvent;
import com.yameizitd.gateway.spoiler.exception.impl.EntryInuseException;
import com.yameizitd.gateway.spoiler.handler.EventHandler;
import com.yameizitd.gateway.spoiler.handler.InstanceHandler;
import com.yameizitd.gateway.spoiler.handler.ServiceHandler;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import com.yameizitd.gateway.spoiler.mapper.RouteMapper;
import com.yameizitd.gateway.spoiler.mapper.ServiceMapper;
import com.yameizitd.gateway.spoiler.mapstruct.ServiceMapstruct;
import com.yameizitd.gateway.spoiler.util.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ServiceHandlerImpl implements ServiceHandler {
    private final ServiceMapper serviceMapper;
    private final RouteMapper routeMapper;
    private final ServiceMapstruct serviceMapstruct;
    private final InstanceHandler instanceHandler;
    private final EventHandler eventHandler;

    public ServiceHandlerImpl(ServiceMapper serviceMapper,
                              RouteMapper routeMapper,
                              ServiceMapstruct serviceMapstruct,
                              InstanceHandler instanceHandler,
                              EventHandler eventHandler) {
        this.serviceMapper = serviceMapper;
        this.routeMapper = routeMapper;
        this.serviceMapstruct = serviceMapstruct;
        this.instanceHandler = instanceHandler;
        this.eventHandler = eventHandler;
    }

    @Transactional
    @Override
    public int create(ServiceCreateForm form) {
        ServiceEntity entity = serviceMapstruct.createForm2entity(form);
        int inserted = serviceMapper.insert(entity);
        if (inserted > 0) {
            eventHandler.publish(RefreshEvent.Operation.SAVE_SERVICES, entity);
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
            instanceHandler.removeByServiceId(id);
            ServiceEntity entity = new ServiceEntity();
            entity.setId(id);
            eventHandler.publish(RefreshEvent.Operation.DELETE_SERVICES, entity);
        }
        return deleted;
    }

    @Transactional
    @Override
    public int edit(ServiceUpdateForm form) {
        ServiceEntity entity = serviceMapstruct.updateForm2entity(form);
        int updated = serviceMapper.update(entity);
        if (updated > 0) {
            eventHandler.publish(RefreshEvent.Operation.DELETE_SERVICES, entity);
            eventHandler.publish(RefreshEvent.Operation.SAVE_SERVICES, entity);
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
            eventHandler.publish(RefreshEvent.Operation.DELETE_SERVICES, entity);
        }
        return disabled;
    }

    @Transactional
    @Override
    public int enable(long id) {
        int enabled = serviceMapper.enable(id);
        if (enabled > 0) {
            eventHandler.publish(RefreshEvent.Operation.SAVE_SERVICES, serviceMapper.selectById(id));
            instanceHandler.enableByServiceId(id);
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
}
