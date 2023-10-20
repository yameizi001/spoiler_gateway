package com.yameizitd.gateway.spoiler.mapstruct;

import com.yameizitd.gateway.spoiler.domain.TemplateType;
import com.yameizitd.gateway.spoiler.domain.entity.TemplateEntity;
import com.yameizitd.gateway.spoiler.domain.form.TemplateUpsertForm;
import com.yameizitd.gateway.spoiler.domain.view.SimpleTemplateView;
import com.yameizitd.gateway.spoiler.domain.view.TemplateDetailView;
import com.yameizitd.gateway.spoiler.util.IdUtils;
import com.yameizitd.gateway.spoiler.util.JacksonUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", uses = JacksonUtils.class)
public interface TemplateMapstruct {
    default TemplateEntity upsertForm2entity(TemplateUpsertForm form) {
        TemplateEntity entity = upsertForm2entity0(form);
        LocalDateTime now = LocalDateTime.now();
        if (form.getId() == null) {
            entity.setId(IdUtils.nextSnowflakeId());
            entity.setCreateTime(now);
        }
        entity.setUpdateTime(now);
        return entity;
    }

    @Mapping(source = "type", target = "type", qualifiedByName = "templateType2short")
    TemplateEntity upsertForm2entity0(TemplateUpsertForm form);

    SimpleTemplateView entity2simpleView(TemplateEntity entity);

    TemplateDetailView entity2detail(TemplateEntity entity);

    @Named("templateType2short")
    default Short templateType2short(TemplateType type) {
        if (type == null) {
            return null;
        }
        return (short) type.ordinal();
    }
}
