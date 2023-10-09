package com.yameizitd.gateway.spoiler.domain.po;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PropertyPO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6005141123278790486L;

    private Long id;
    private Long elementId;
    private String alias;
    private String description;
    private String key;
    private Boolean required;
    private String regex;
}
