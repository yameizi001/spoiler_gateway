package com.yameizitd.gateway.spoiler.domain.form;

import com.yameizitd.gateway.spoiler.domain.ElementType;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ElementCreateForm implements Serializable {
    @Serial
    private static final long serialVersionUID = 2803246586107245727L;

    private String name;
    private String alias;
    private String icon;
    private String description;
    private Integer ordered;
    private ElementType type;
}
