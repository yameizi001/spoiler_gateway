package com.yameizitd.gateway.spoiler.domain.view;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private List<ElementView> predicates = new ArrayList<>();
    private List<ElementView> filters = new ArrayList<>();
    private List<PropertyValuesView> metadata = new ArrayList<>();
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
