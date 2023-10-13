package com.yameizitd.gateway.spoiler.domain.entity;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RichRouteEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -2420646781425317425L;

    private Long id;
    private Long serviceId;
    private String serviceName;
    private Long templateId;
    private String templateName;
    private String name;
    private String description;
    private String predicates;
    private String filters;
    private Integer ordered;
    private String metadata;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean enabled;
}
