package com.yameizitd.gateway.spoiler.domain.entity;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PropertyValuesEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 6454658858652776648L;

    private Long id;
    private String values;
}
