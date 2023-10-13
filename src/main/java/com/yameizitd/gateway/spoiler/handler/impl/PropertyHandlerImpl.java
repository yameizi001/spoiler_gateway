package com.yameizitd.gateway.spoiler.handler.impl;

import com.yameizitd.gateway.spoiler.domain.entity.ElementEntity;
import com.yameizitd.gateway.spoiler.domain.entity.PropertyEntity;
import com.yameizitd.gateway.spoiler.domain.form.PropertyCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.PropertyQueryForm;
import com.yameizitd.gateway.spoiler.domain.view.PropertyView;
import com.yameizitd.gateway.spoiler.exception.impl.EntryNotExistException;
import com.yameizitd.gateway.spoiler.handler.PropertyHandler;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import com.yameizitd.gateway.spoiler.mapper.ElementMapper;
import com.yameizitd.gateway.spoiler.mapper.PropertyMapper;
import com.yameizitd.gateway.spoiler.mapstruct.PropertyMapstruct;
import com.yameizitd.gateway.spoiler.util.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PropertyHandlerImpl implements PropertyHandler {
    private final PropertyMapper propertyMapper;
    private final ElementMapper elementMapper;
    private final PropertyMapstruct propertyMapstruct;

    public PropertyHandlerImpl(PropertyMapper propertyMapper,
                               ElementMapper elementMapper,
                               PropertyMapstruct propertyMapstruct) {
        this.propertyMapper = propertyMapper;
        this.elementMapper = elementMapper;
        this.propertyMapstruct = propertyMapstruct;
    }

    @Transactional
    @Override
    public int create(PropertyCreateForm form) {
        checkPropertyAssociatedEntity(form.getElementId());
        PropertyEntity entity = propertyMapstruct.createForm2entity(form);
        return propertyMapper.insert(entity);
    }

    private void checkPropertyAssociatedEntity(Long elementId) {
        if (elementId == null) {
            return;
        }
        ElementEntity record = elementMapper.selectByIdForUpdate(elementId);
        if (record == null) {
            throw new EntryNotExistException("Element not exist");
        }
    }

    @Transactional
    @Override
    public int remove(long id) {
        // todo: check property is inuse
        return propertyMapper.delete(id);
    }

    @Override
    public IPage<PropertyView> getPageableByOptions(PropertyQueryForm query) {
        IPage<PropertyEntity> page = query.getPage().iPage();
        List<PropertyEntity> records = propertyMapper.selectByOptions(query, page);
        page.setRecords(records);
        return PageUtils.map(page, propertyMapstruct::entity2view);
    }
}
