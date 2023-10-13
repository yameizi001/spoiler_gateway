package com.yameizitd.gateway.spoiler.domain.form;

import com.yameizitd.gateway.spoiler.domain.TemplateType;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class TemplateQueryForm implements Serializable {
    @Serial
    private static final long serialVersionUID = -2697484161057258452L;

    private Long id;
    private String name;
    private TemplateType type;
    private Page page;
}
