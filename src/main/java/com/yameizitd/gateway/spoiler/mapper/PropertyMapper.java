package com.yameizitd.gateway.spoiler.mapper;

import com.yameizitd.gateway.spoiler.domain.entity.PropertyEntity;
import com.yameizitd.gateway.spoiler.domain.form.PropertyQueryForm;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PropertyMapper {
    int insert(@Param("entity") PropertyEntity entity);

    int delete(Long id);

    int deleteByElementId(Long elementId);

    boolean inuse(Long id);

    PropertyEntity selectByIdAndElementIdForUpdate(@Param("id") Long id,
                                                   @Param("elementId") Long elementId);

    List<PropertyEntity> selectByOptions(@Param("query") PropertyQueryForm query,
                                         @Param("pgPage") IPage<PropertyEntity> page);
}
