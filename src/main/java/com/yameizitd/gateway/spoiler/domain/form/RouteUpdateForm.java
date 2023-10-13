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
public class RouteUpdateForm implements Serializable {
    @Serial
    private static final long serialVersionUID = -7937256291421793677L;

    private Long id;
    private Long serviceId;
    private Long templateId;
    private String name;
    private String description;
    private JsonNode predicates;
    private JsonNode filters;
    private Integer ordered;
    private JsonNode metadata;
}
