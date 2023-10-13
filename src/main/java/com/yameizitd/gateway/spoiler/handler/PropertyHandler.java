package com.yameizitd.gateway.spoiler.handler;

import com.yameizitd.gateway.spoiler.domain.form.PropertyCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.PropertyQueryForm;
import com.yameizitd.gateway.spoiler.domain.view.PropertyView;
import com.yameizitd.gateway.spoiler.interceptor.IPage;

public interface PropertyHandler {
    int create(PropertyCreateForm form);

    int remove(long id);

    IPage<PropertyView> getPageableByOptions(PropertyQueryForm query);
}
