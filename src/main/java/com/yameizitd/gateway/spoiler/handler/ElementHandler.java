package com.yameizitd.gateway.spoiler.handler;

import com.yameizitd.gateway.spoiler.domain.form.ElementCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.ElementQueryForm;
import com.yameizitd.gateway.spoiler.domain.view.ElementView;
import com.yameizitd.gateway.spoiler.interceptor.IPage;

public interface ElementHandler {
    int create(ElementCreateForm form);

    int remove(long id);

    IPage<ElementView> getPageableByOptions(ElementQueryForm query);
}
