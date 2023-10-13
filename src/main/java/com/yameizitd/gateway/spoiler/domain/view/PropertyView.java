package com.yameizitd.gateway.spoiler.domain.view;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PropertyView implements Serializable {
    @Serial
    private static final long serialVersionUID = -7858109543169744424L;

    private String id;
    private String elementId;
    private String alias;
    private String description;
    private String key;
    private Boolean required;
    private String regex;
}
