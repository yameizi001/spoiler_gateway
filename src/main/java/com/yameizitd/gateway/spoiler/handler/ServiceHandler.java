package com.yameizitd.gateway.spoiler.handler;

import com.yameizitd.gateway.spoiler.domain.form.ServiceCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.ServiceQueryForm;
import com.yameizitd.gateway.spoiler.domain.form.ServiceUpdateForm;
import com.yameizitd.gateway.spoiler.domain.view.ServiceView;
import com.yameizitd.gateway.spoiler.interceptor.IPage;

public interface ServiceHandler {
    int create(ServiceCreateForm form);

    int remove(long id);

    int edit(ServiceUpdateForm form);

    int disable(long id);

    int enable(long id);

    IPage<ServiceView> getPageableByOptions(ServiceQueryForm query);
}
