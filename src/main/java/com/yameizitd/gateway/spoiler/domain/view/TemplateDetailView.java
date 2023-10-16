package com.yameizitd.gateway.spoiler.domain.view;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class TemplateDetailView implements Serializable {
    @Serial
    private static final long serialVersionUID = 4671473436186217989L;

    private String id;
    private String name;
    private String description;
    private List<ElementView> predicates;
    private List<ElementView> filters;
    private List<PropertyValuesView> metadata;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
