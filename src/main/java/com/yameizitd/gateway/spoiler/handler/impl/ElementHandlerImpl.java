package com.yameizitd.gateway.spoiler.handler.impl;

import com.yameizitd.gateway.spoiler.domain.entity.ElementEntity;
import com.yameizitd.gateway.spoiler.domain.form.ElementCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.ElementQueryForm;
import com.yameizitd.gateway.spoiler.domain.view.ElementView;
import com.yameizitd.gateway.spoiler.exception.impl.EntryInuseException;
import com.yameizitd.gateway.spoiler.handler.ElementHandler;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import com.yameizitd.gateway.spoiler.mapper.ElementMapper;
import com.yameizitd.gateway.spoiler.mapper.FileMapper;
import com.yameizitd.gateway.spoiler.mapper.PropertyMapper;
import com.yameizitd.gateway.spoiler.mapstruct.ElementMapstruct;
import com.yameizitd.gateway.spoiler.util.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ElementHandlerImpl implements ElementHandler {
    private final ElementMapper elementMapper;
    private final PropertyMapper propertyMapper;
    private final FileMapper fileMapper;
    private final ElementMapstruct elementMapstruct;

    public ElementHandlerImpl(ElementMapper elementMapper,
                              PropertyMapper propertyMapper,
                              FileMapper fileMapper,
                              ElementMapstruct elementMapstruct) {
        this.elementMapper = elementMapper;
        this.propertyMapper = propertyMapper;
        this.fileMapper = fileMapper;
        this.elementMapstruct = elementMapstruct;
    }

    @Transactional
    @Override
    public int create(ElementCreateForm form) {
        ElementEntity entity = elementMapstruct.createForm2entity(form);
        return elementMapper.insert(entity);
    }

    @Transactional
    @Override
    public int remove(long id) {
        // check element is inuse
        boolean inuse = elementMapper.inuse(id);
        if (inuse) {
            throw new EntryInuseException("Element is inuse");
        }
        fileMapper.deleteByElementId(id);
        int deleted = elementMapper.delete(id);
        if (deleted > 0) {
            propertyMapper.deleteByElementId(id);
        }
        return deleted;
    }

    @Override
    public IPage<ElementView> getPageableByOptions(ElementQueryForm query) {
        IPage<ElementEntity> page = query.getPage().iPage();
        List<ElementEntity> records = elementMapper.selectByOptions(
                query,
                elementMapstruct.elementType2short(query.getType()),
                page
        );
        page.setRecords(records);
        return PageUtils.map(page, elementMapstruct::entity2view);
    }
}
