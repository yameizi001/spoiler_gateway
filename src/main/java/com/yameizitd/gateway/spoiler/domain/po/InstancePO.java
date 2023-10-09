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
public class InstancePO implements Serializable {
    @Serial
    private static final long serialVersionUID = 9065429610260913519L;

    private Long id;
    private Long serviceId;
    private String name;
    private String scheme;
    private Boolean secure;
    private String host;
    private Integer port;
    private Integer weight;
    private Boolean health;
    private String metadata;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean enabled;
}
