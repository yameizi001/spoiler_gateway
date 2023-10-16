package com.yameizitd.gateway.spoiler.mapper;

import com.yameizitd.gateway.spoiler.domain.entity.PropertyValuesEntity;
import com.yameizitd.gateway.spoiler.domain.entity.RichPropertyValuesEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TemplateElementPropertyMapper {
    int batchInsertElementsByTemplateId(@Param("templateId") Long templateId,
                                        @Param("elementIds") List<Long> elementIds);

    int batchInsertPropertiesByTemplateIdAndElementId(@Param("templateId") Long templateId,
                                                      @Param("elementId") Long elementId,
                                                      @Param("properties") List<PropertyValuesEntity> properties);

    int batchInsertPropertiesByTemplateId(@Param("templateId") Long templateId,
                                          @Param("properties") List<PropertyValuesEntity> properties);

    int deleteElementsByTemplateId(Long templateId);

    int deleteElementPropertiesByTemplateId(Long templateId);

    int deletePropertiesByTemplateId(Long templateId);

    List<RichPropertyValuesEntity> selectPropertiesByTemplateId(Long templateId);

    List<RichPropertyValuesEntity> selectPropertiesByTemplateIdAndElementId(@Param("templateId") Long templateId,
                                                                            @Param("elementId") Long elementId);
}
