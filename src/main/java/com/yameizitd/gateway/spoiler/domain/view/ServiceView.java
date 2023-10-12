package com.yameizitd.gateway.spoiler.domain.view;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ServiceView implements Serializable {
    @Serial
    private static final long serialVersionUID = 5547629017747763459L;

    private String id;
    private String name;
    private String description;
    private JsonNode metadata;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean enabled;
}
