package com.yameizitd.gateway.spoiler.domain.entity;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class TemplateEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -1780272899679588067L;

    private Long id;
    private String name;
    private String description;
    private Short type;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
