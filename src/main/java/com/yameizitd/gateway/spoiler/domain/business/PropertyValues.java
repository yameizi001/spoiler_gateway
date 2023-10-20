package com.yameizitd.gateway.spoiler.domain.business;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PropertyValues implements Serializable {
    @Serial
    private static final long serialVersionUID = 9042964463233681126L;

    private String id;
    private String elementId;
    private String alias;
    private String description;
    private String key;
    private Boolean required;
    private String regex;
    private JsonNode values;
}
