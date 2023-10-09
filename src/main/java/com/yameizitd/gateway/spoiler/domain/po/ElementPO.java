package com.yameizitd.gateway.spoiler.domain.po;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ElementPO implements Serializable {
    @Serial
    private static final long serialVersionUID = -9194760408180689731L;

    private Long id;
    private String name;
    private String alias;
    private String icon;
    private String description;
    private Integer ordered;
    private Short type;
}
