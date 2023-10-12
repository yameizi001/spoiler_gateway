package com.yameizitd.gateway.spoiler.domain.form;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ServiceQueryForm implements Serializable {
    @Serial
    private static final long serialVersionUID = -2563491274898624371L;

    private Long id;
    private String name;
    private Boolean enabled;
    private Page page;
}
