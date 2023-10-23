package com.yameizitd.gateway.spoiler.domain.form;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RouteWithTemplateUpsertForm implements Serializable {
    @Serial
    private static final long serialVersionUID = 9184652979081924081L;

    private Long id;
    private Long serviceId;
    private String name;
    private String description;
    private TemplateUpsertForm template;
    private Integer ordered;
    private LocalDateTime createTime;
}
