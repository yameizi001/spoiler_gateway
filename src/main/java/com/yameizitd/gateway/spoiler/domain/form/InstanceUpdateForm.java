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
public class InstanceUpdateForm implements Serializable {
    @Serial
    private static final long serialVersionUID = -2155080660744271164L;

    private Long id;
    private String name;
    private String scheme;
    private Boolean secure;
    private String host;
    private Integer port;
    private Integer weight;
    private Boolean health;
    private JsonNode metadata;
}
