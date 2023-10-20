package com.yameizitd.gateway.spoiler.mapstruct;

import com.yameizitd.gateway.spoiler.domain.business.PropertyValues;
import com.yameizitd.gateway.spoiler.domain.entity.PropertyEntity;
import com.yameizitd.gateway.spoiler.domain.entity.PropertyValuesEntity;
import com.yameizitd.gateway.spoiler.domain.entity.RichPropertyValuesEntity;
import com.yameizitd.gateway.spoiler.domain.form.PropertyValuesCreateForm;
import com.yameizitd.gateway.spoiler.domain.view.PropertyValuesView;
import com.yameizitd.gateway.spoiler.util.JacksonUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = JacksonUtils.class)
public interface PropertyValuesMapstruct {
    @Mapping(source = "values", target = "values", qualifiedByName = "jsonNode2string")
    PropertyValuesEntity createForm2entity(PropertyValuesCreateForm form);

    @Mapping(source = "values", target = "values", qualifiedByName = "string2jsonNode")
    PropertyValuesView richEntity2view(RichPropertyValuesEntity entity);

    PropertyValues entity2definition(PropertyEntity entity);
}
