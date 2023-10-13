package com.yameizitd.gateway.spoiler.domain.entity;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RichPropertyValuesEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 4056840435016193859L;

    private Long id;
    private Long elementId;
    private String alias;
    private String description;
    private String key;
    private Boolean required;
    private String regex;
    private String values;
}
