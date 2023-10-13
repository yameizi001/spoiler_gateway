package com.yameizitd.gateway.spoiler.mapstruct;

import com.yameizitd.gateway.spoiler.domain.ElementType;
import com.yameizitd.gateway.spoiler.domain.entity.ElementEntity;
import com.yameizitd.gateway.spoiler.domain.form.ElementCreateForm;
import com.yameizitd.gateway.spoiler.domain.view.ElementView;
import com.yameizitd.gateway.spoiler.util.IdUtils;
import com.yameizitd.gateway.spoiler.util.JacksonUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = JacksonUtils.class)
public interface ElementMapstruct {
    default ElementEntity createForm2entity(ElementCreateForm form) {
        ElementEntity entity = createForm2entity0(form);
        entity.setId(IdUtils.nextSnowflakeId());
        return entity;
    }

    @Mapping(source = "type", target = "type", qualifiedByName = "elementType2short")
    ElementEntity createForm2entity0(ElementCreateForm form);

    @Mapping(source = "type", target = "type", qualifiedByName = "short2elementType")
    ElementView entity2view(ElementEntity entity);

    @Named("elementType2short")
    default Short elementType2short(ElementType type) {
        if (type == null)
            return null;
        return (short) type.ordinal();
    }

    @Named("short2elementType")
    default ElementType short2elementType(Short ordinal) {
        return ElementType.values()[ordinal];
    }
}
