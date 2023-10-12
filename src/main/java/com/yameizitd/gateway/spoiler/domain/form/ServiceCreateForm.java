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
public class ServiceCreateForm implements Serializable {
    @Serial
    private static final long serialVersionUID = 2718971563289785115L;

    private String name;
    private String description;
    private JsonNode metadata;
}
