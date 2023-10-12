package com.yameizitd.gateway.spoiler.handler;

import com.yameizitd.gateway.spoiler.domain.form.InstanceCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.InstanceQueryForm;
import com.yameizitd.gateway.spoiler.domain.form.InstanceUpdateForm;
import com.yameizitd.gateway.spoiler.domain.view.InstanceView;
import com.yameizitd.gateway.spoiler.interceptor.IPage;

public interface InstanceHandler {
    int create(InstanceCreateForm form);

    int remove(long id);

    int removeByServiceId(long serviceId);

    int edit(InstanceUpdateForm form);

    int disable(long id);

    int enable(long id);

    int enableByServiceId(long serviceId);

    IPage<InstanceView> getPageableByOptions(InstanceQueryForm form);
}
