package com.yameizitd.gateway.spoiler.handler.impl;

import com.yameizitd.gateway.spoiler.domain.form.ElementCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.ElementQueryForm;
import com.yameizitd.gateway.spoiler.domain.form.ElementUpdateForm;
import com.yameizitd.gateway.spoiler.domain.view.ElementView;
import com.yameizitd.gateway.spoiler.handler.ElementHandler;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import com.yameizitd.gateway.spoiler.mapper.ElementMapper;
import com.yameizitd.gateway.spoiler.mapstruct.ElementMapstruct;
import org.springframework.stereotype.Service;

@Service
public class ElementHandlerImpl implements ElementHandler {
    private final ElementMapper elementMapper;
    private final ElementMapstruct elementMapstruct;

    public ElementHandlerImpl(ElementMapper elementMapper, ElementMapstruct elementMapstruct) {
        this.elementMapper = elementMapper;
        this.elementMapstruct = elementMapstruct;
    }

    @Override
    public int create(ElementCreateForm form) {
        return 0;
    }

    @Override
    public int remove(long id) {
        return 0;
    }

    @Override
    public int update(ElementUpdateForm form) {
        return 0;
    }

    @Override
    public IPage<ElementView> getByOptions(ElementQueryForm query) {
        return null;
    }
}
