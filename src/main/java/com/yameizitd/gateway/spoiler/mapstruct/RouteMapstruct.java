package com.yameizitd.gateway.spoiler.mapstruct;

import com.yameizitd.gateway.spoiler.util.JacksonUtils;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = JacksonUtils.class)
public interface RouteMapstruct {
}
