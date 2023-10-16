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
public class RouteView implements Serializable {
    @Serial
    private static final long serialVersionUID = -2420646781425317425L;

    private String id;
    private String serviceId;
    private String serviceName;
    private String templateId;
    private String templateName;
    private String name;
    private String description;
    private JsonNode predicates;
    private JsonNode filters;
    private Integer ordered;
    private JsonNode metadata;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean enabled;
}
