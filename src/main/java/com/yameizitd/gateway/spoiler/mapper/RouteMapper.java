package com.yameizitd.gateway.spoiler.mapper;

import com.yameizitd.gateway.spoiler.domain.entity.RouteEntity;
import com.yameizitd.gateway.spoiler.domain.form.RouteQueryForm;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RouteMapper {
    int insert(@Param("entity") RouteEntity entity);

    int delete(Long id);

    int edit(@Param("entity") RouteEntity entity);

    int disable(Long id);

    int enable(Long id);

    RouteEntity selectById(Long id);

    List<RouteEntity> selectByOptions(@Param("query") RouteQueryForm query,
                                      @Param("pgPage") IPage<RouteEntity> page);
}
