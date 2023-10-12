package com.yameizitd.gateway.spoiler.mapstruct;

import com.yameizitd.gateway.spoiler.domain.business.ServiceDefinition;
import com.yameizitd.gateway.spoiler.domain.entity.ServiceEntity;
import com.yameizitd.gateway.spoiler.domain.form.ServiceCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.ServiceUpdateForm;
import com.yameizitd.gateway.spoiler.domain.view.ServiceView;
import com.yameizitd.gateway.spoiler.util.IdUtils;
import com.yameizitd.gateway.spoiler.util.JacksonUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", uses = JacksonUtils.class)
public interface ServiceMapstruct {
    default ServiceEntity createForm2entity(ServiceCreateForm form) {
        ServiceEntity entity = createForm2entity0(form);
        entity.setId(IdUtils.nextSnowflakeId());
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        entity.setEnabled(true);
        return entity;
    }

    @Mapping(source = "metadata", target = "metadata", qualifiedByName = "jsonNode2string")
    ServiceEntity createForm2entity0(ServiceCreateForm form);

    default ServiceEntity updateForm2entity(ServiceUpdateForm form) {
        ServiceEntity entity = updateForm2entity0(form);
        entity.setUpdateTime(LocalDateTime.now());
        return entity;
    }

    @Mapping(source = "metadata", target = "metadata", qualifiedByName = "jsonNode2string")
    ServiceEntity updateForm2entity0(ServiceUpdateForm form);

    @Mapping(source = "metadata", target = "metadata", qualifiedByName = "string2jsonNode")
    ServiceView entity2view(ServiceEntity entity);

    @Mapping(source = "metadata", target = "metadata", qualifiedByName = "string2map")
    ServiceDefinition entity2definition(ServiceEntity entity);
}
