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
public class PropertyValuesCreateForm implements Serializable {
    @Serial
    private static final long serialVersionUID = 7584822857935773873L;

    private Long id;
    private String alias;
    private JsonNode values;
}
