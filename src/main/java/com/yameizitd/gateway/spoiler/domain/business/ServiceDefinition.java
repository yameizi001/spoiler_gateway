package com.yameizitd.gateway.spoiler.domain.business;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ServiceDefinition implements Serializable {
    @Serial
    private static final long serialVersionUID = -7803642348531498113L;

    private String id;
    private Map<String, String> metadata;
}
