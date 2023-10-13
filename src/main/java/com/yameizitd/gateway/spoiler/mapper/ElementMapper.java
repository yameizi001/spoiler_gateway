package com.yameizitd.gateway.spoiler.mapper;

import com.yameizitd.gateway.spoiler.domain.entity.ElementEntity;
import com.yameizitd.gateway.spoiler.domain.form.ElementQueryForm;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ElementMapper {
    int insert(@Param("entity") ElementEntity entity);

    int delete(Long id);

    ElementEntity selectById(Long id);

    ElementEntity selectByIdForUpdate(Long elementId);

    boolean inuse(Long id);

    List<ElementEntity> selectByTemplateIdAndType(@Param("templateId") Long templateId,
                                                  @Param("type") Short type);

    List<ElementEntity> selectByOptions(@Param("query") ElementQueryForm query,
                                        @Param("type") Short type,
                                        @Param("pgPage") IPage<ElementEntity> page);

}
