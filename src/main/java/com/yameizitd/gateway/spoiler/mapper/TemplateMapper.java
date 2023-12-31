package com.yameizitd.gateway.spoiler.mapper;

import com.yameizitd.gateway.spoiler.domain.entity.TemplateEntity;
import com.yameizitd.gateway.spoiler.domain.form.TemplateQueryForm;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TemplateMapper {
    int upsert(@Param("entity") TemplateEntity entity);

    int delete(Long id);

    int deleteByIdAndType(@Param("id") Long id, @Param("type") Short type);

    TemplateEntity selectById(Long id);

    List<TemplateEntity> selectByOptions(@Param("query") TemplateQueryForm query,
                                         @Param("type") Short type,
                                         @Param("pgPage") IPage<TemplateEntity> page);
}
