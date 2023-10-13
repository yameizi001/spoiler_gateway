package com.yameizitd.gateway.spoiler.domain.form;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PropertyCreateForm implements Serializable {
    @Serial
    private static final long serialVersionUID = 4659776349617397679L;

    private Long elementId;
    private String alias;
    private String description;
    private String key;
    private Boolean required;
    private String regex;
}
