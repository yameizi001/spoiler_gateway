package com.yameizitd.gateway.spoiler.domain.form;

import com.yameizitd.gateway.spoiler.domain.TemplateType;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class TemplateUpsertForm implements Serializable {
    @Serial
    private static final long serialVersionUID = -2528570753484240244L;

    private Long id;
    private String name;
    private String description;
    private List<ElementWithPropertiesCreateForm> predicates;
    private List<ElementWithPropertiesCreateForm> filters;
    private List<PropertyValuesCreateForm> metadata;
    private TemplateType type;
}
