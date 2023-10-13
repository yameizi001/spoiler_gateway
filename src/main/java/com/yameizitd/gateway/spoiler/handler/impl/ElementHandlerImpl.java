package com.yameizitd.gateway.spoiler.handler.impl;

import com.yameizitd.gateway.spoiler.domain.entity.ElementEntity;
import com.yameizitd.gateway.spoiler.domain.form.ElementCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.ElementQueryForm;
import com.yameizitd.gateway.spoiler.domain.view.ElementView;
import com.yameizitd.gateway.spoiler.handler.ElementHandler;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import com.yameizitd.gateway.spoiler.mapper.ElementMapper;
import com.yameizitd.gateway.spoiler.mapper.PropertyMapper;
import com.yameizitd.gateway.spoiler.mapstruct.ElementMapstruct;
import com.yameizitd.gateway.spoiler.util.PageUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElementHandlerImpl implements ElementHandler {
    private final ElementMapper elementMapper;
    private final PropertyMapper propertyMapper;
    private final ElementMapstruct elementMapstruct;

    public ElementHandlerImpl(ElementMapper elementMapper,
                              PropertyMapper propertyMapper,
                              ElementMapstruct elementMapstruct) {
        this.elementMapper = elementMapper;
        this.propertyMapper = propertyMapper;
        this.elementMapstruct = elementMapstruct;
    }

    @Override
    public int create(ElementCreateForm form) {
        ElementEntity entity = elementMapstruct.createForm2entity(form);
        return elementMapper.insert(entity);
    }

    @Override
    public int remove(long id) {
        // todo: check element is inuse
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
