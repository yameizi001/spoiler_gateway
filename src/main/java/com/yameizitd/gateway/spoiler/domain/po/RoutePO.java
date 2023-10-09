package com.yameizitd.gateway.spoiler.domain.po;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RoutePO implements Serializable {
    @Serial
    private static final long serialVersionUID = -8019568147975917259L;

    private Long id;
    private Long serviceId;
    private Long templateId;
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
