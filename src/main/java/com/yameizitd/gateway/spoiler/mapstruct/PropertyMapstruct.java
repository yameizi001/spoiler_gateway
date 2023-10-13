package com.yameizitd.gateway.spoiler.mapstruct;

import com.yameizitd.gateway.spoiler.domain.entity.PropertyEntity;
import com.yameizitd.gateway.spoiler.domain.form.PropertyCreateForm;
import com.yameizitd.gateway.spoiler.domain.view.PropertyView;
import com.yameizitd.gateway.spoiler.util.IdUtils;
import com.yameizitd.gateway.spoiler.util.JacksonUtils;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = JacksonUtils.class)
public interface PropertyMapstruct {
    default PropertyEntity createForm2entity(PropertyCreateForm form) {
        PropertyEntity entity = createForm2entity0(form);
        entity.setId(IdUtils.nextSnowflakeId());
        if (entity.getElementId() == null) {
            entity.setElementId(-1L);
        }
        return entity;
    }

    PropertyEntity createForm2entity0(PropertyCreateForm form);

    PropertyView entity2view(PropertyEntity entity);
}
