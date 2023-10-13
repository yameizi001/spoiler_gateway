package com.yameizitd.gateway.spoiler.mapper;

import com.yameizitd.gateway.spoiler.domain.entity.ServiceEntity;
import com.yameizitd.gateway.spoiler.domain.form.ServiceQueryForm;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ServiceMapper {
    int insert(@Param("entity") ServiceEntity entity);

    int delete(Long id);

    int update(@Param("entity") ServiceEntity entity);

    int disable(Long id);

    int enable(Long id);

    ServiceEntity selectById(Long id);

    ServiceEntity selectByIdForUpdate(Long id);

    List<ServiceEntity> selectByOptions(@Param("query") ServiceQueryForm query,
                                        @Param("pgPage") IPage<ServiceEntity> page);
}
