package com.yameizitd.gateway.spoiler.domain.form;

import com.yameizitd.gateway.spoiler.domain.TemplateType;
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
public class TemplateUpsertForm implements Serializable {
    @Serial
    private static final long serialVersionUID = -2528570753484240244L;

    private Long id;
    private String name;
    private String description;
    private List<ElementWithPropertiesCreateForm> predicates = new ArrayList<>();
    private List<ElementWithPropertiesCreateForm> filters = new ArrayList<>();
    private List<PropertyValuesCreateForm> metadata = new ArrayList<>();
    private TemplateType type;
    private LocalDateTime createTime;
}
