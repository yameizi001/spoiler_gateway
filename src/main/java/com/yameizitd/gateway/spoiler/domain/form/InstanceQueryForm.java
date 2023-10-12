package com.yameizitd.gateway.spoiler.domain.form;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class InstanceQueryForm implements Serializable {
    @Serial
    private static final long serialVersionUID = -135396151725808116L;

    private Long id;
    private Long serviceId;
    private String name;
    private String scheme;
    private Boolean secure;
    private String host;
    private Integer port;
    private Boolean health;
    private Boolean enabled;
    private Page page;
}
