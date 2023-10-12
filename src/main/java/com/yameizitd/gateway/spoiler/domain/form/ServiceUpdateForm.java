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
public class ServiceUpdateForm implements Serializable {
    @Serial
    private static final long serialVersionUID = 4334761617016339758L;

    private Long id;
    private String name;
    private String description;
    private JsonNode metadata;
}
