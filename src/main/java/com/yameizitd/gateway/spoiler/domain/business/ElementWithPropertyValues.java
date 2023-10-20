package com.yameizitd.gateway.spoiler.domain.business;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ElementWithPropertyValues implements Serializable {
    @Serial
    private static final long serialVersionUID = -2275777377169234370L;

    private String name;
    private Integer ordered;
    private List<PropertyValues> propertyValues = new ArrayList<>();
}
