package com.yameizitd.gateway.spoiler.handler;

import com.yameizitd.gateway.spoiler.domain.form.TemplateQueryForm;
import com.yameizitd.gateway.spoiler.domain.form.TemplateUpsertForm;
import com.yameizitd.gateway.spoiler.domain.view.PropertyValuesView;
import com.yameizitd.gateway.spoiler.domain.view.SimpleTemplateView;
import com.yameizitd.gateway.spoiler.domain.view.TemplateDetail;
import com.yameizitd.gateway.spoiler.interceptor.IPage;

import java.util.List;

public interface TemplateHandler {
    int create(TemplateUpsertForm form);

    int remove(long id);

    int edit(TemplateUpsertForm form);

    IPage<SimpleTemplateView> getPageableByOptions(TemplateQueryForm query);

    TemplateDetail getDetailById(long id);

    List<PropertyValuesView> getPropertiesByTemplateIdAndElementId(long templateId, long elementId);
}
