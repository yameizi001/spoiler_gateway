package com.yameizitd.gateway.spoiler.domain.view;

import com.yameizitd.gateway.spoiler.domain.ElementType;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ElementView implements Serializable {
    @Serial
    private static final long serialVersionUID = 4448467650390181729L;

    private String id;
    private String name;
    private String alias;
    private String icon;
    private String description;
    private Integer ordered;
    private ElementType type;
    private List<PropertyValuesView> properties;
}
