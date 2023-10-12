package com.yameizitd.gateway.spoiler.mapstruct;

import com.yameizitd.gateway.spoiler.domain.business.InstanceDefinition;
import com.yameizitd.gateway.spoiler.domain.entity.InstanceEntity;
import com.yameizitd.gateway.spoiler.domain.form.InstanceCreateForm;
import com.yameizitd.gateway.spoiler.domain.form.InstanceUpdateForm;
import com.yameizitd.gateway.spoiler.domain.view.InstanceView;
import com.yameizitd.gateway.spoiler.util.IdUtils;
import com.yameizitd.gateway.spoiler.util.JacksonUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.cloud.client.ServiceInstance;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper(componentModel = "spring", uses = JacksonUtils.class)
public interface InstanceMapstruct {
    default InstanceEntity createForm2entity(InstanceCreateForm form) {
        InstanceEntity entity = createForm2entity0(form);
        entity.setId(IdUtils.nextSnowflakeId());
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        entity.setEnabled(true);
        return entity;
    }

    @Mapping(source = "metadata", target = "metadata", qualifiedByName = "jsonNode2string")
    InstanceEntity createForm2entity0(InstanceCreateForm form);

    default InstanceEntity updateForm2entity(InstanceUpdateForm form) {
        InstanceEntity entity = updateForm2entity0(form);
        entity.setUpdateTime(LocalDateTime.now());
        return entity;
    }

    @Mapping(source = "metadata", target = "metadata", qualifiedByName = "jsonNode2string")
    InstanceEntity updateForm2entity0(InstanceUpdateForm form);

    @Mapping(source = "metadata", target = "metadata", qualifiedByName = "string2jsonNode")
    InstanceView entity2view(InstanceEntity entity);

    default ServiceInstance entity2definition(InstanceEntity entity) {
        ServiceInstance instance = entity2definition0(entity);
        Map<String, String> metadata = instance.getMetadata();
        if (metadata != null) {
            metadata.put(InstanceDefinition.INSTANCE_WEIGHT, String.valueOf(entity.getWeight()));
            metadata.put(InstanceDefinition.INSTANCE_HEALTH, String.valueOf(entity.getHealth()));
        }
        return instance;
    }

    @Mapping(source = "id", target = "instanceId")
    @Mapping(source = "metadata", target = "metadata", qualifiedByName = "string2map")
    InstanceDefinition entity2definition0(InstanceEntity entity);
}
