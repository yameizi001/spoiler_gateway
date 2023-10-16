package com.yameizitd.gateway.spoiler.mapper;

import com.yameizitd.gateway.spoiler.domain.entity.FileEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FileMapper {
    int insert(@Param("entity") FileEntity entity);

    int delete(Long id);

    FileEntity selectById(Long id);
}