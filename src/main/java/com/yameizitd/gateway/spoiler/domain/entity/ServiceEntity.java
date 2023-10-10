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
public class ServiceEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -6252000158822157495L;

    private Long id;
    private String name;
    private String description;
    private String metadata;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean enabled;
}
