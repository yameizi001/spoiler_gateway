package com.yameizitd.gateway.spoiler.domain.form;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RouteCreateForm implements Serializable {
    @Serial
    private static final long serialVersionUID = -8046620711838018786L;

    private Long serviceId;
    private Long templateId;
    private String name;
    private String description;
    private JsonNode predicates;
    private JsonNode filters;
    private Integer ordered;
    private JsonNode metadata;
}
