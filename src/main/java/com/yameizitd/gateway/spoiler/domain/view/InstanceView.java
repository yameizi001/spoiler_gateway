package com.yameizitd.gateway.spoiler.domain.view;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class InstanceView implements Serializable {
    @Serial
    private static final long serialVersionUID = 278024176051179519L;

    private String id;
    private String serviceId;
    private String name;
    private String scheme;
    private Boolean secure;
    private String host;
    private Integer port;
    private Integer weight;
    private Boolean health;
    private JsonNode metadata;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean enabled;
}
