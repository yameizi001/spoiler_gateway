package com.yameizitd.gateway.spoiler.domain.form;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ElementWithPropertiesCreateForm implements Serializable {
    @Serial
    private static final long serialVersionUID = -9025438486862001736L;

    private Long id;
    private String alias;
    private List<PropertyValuesCreateForm> properties;
}
