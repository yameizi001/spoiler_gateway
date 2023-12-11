package com.yameizitd.gateway.spoiler.domain.view;

import com.yameizitd.gateway.spoiler.domain.TemplateType;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SimpleTemplateView implements Serializable {
    @Serial
    private static final long serialVersionUID = 430738677497868166L;

    private String id;
    private String name;
    private String description;
    private TemplateType type;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
