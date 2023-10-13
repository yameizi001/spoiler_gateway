package com.yameizitd.gateway.spoiler.domain.view;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PropertyValuesView implements Serializable {
    @Serial
    private static final long serialVersionUID = -3010988298566638892L;

    private String id;
    private String elementId;
    private String alias;
    private String description;
    private String key;
    private Boolean required;
    private String regex;
    private JsonNode values;
}
