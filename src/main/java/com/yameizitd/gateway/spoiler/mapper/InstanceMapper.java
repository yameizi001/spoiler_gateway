package com.yameizitd.gateway.spoiler.mapper;

import com.yameizitd.gateway.spoiler.domain.entity.InstanceEntity;
import com.yameizitd.gateway.spoiler.domain.form.InstanceQueryForm;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InstanceMapper {
    int insert(@Param("entity") InstanceEntity entity);

    int delete(Long id);

    int deleteByServiceId(Long serviceId);

    int update(@Param("entity") InstanceEntity entity);

    int disable(Long id);

    int enable(Long id);

    InstanceEntity selectById(Long id);

    List<InstanceEntity> selectEnabledByServiceId(Long serviceId);

    List<InstanceEntity> selectByOptions(@Param("query") InstanceQueryForm query,
                                         @Param("pgPage") IPage<InstanceEntity> page);
}
