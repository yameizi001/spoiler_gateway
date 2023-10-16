package com.yameizitd.gateway.spoiler.domain.form;

import com.yameizitd.gateway.spoiler.domain.ElementType;
import com.yameizitd.gateway.spoiler.domain.Page;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ElementQueryForm implements Serializable {
    @Serial
    private static final long serialVersionUID = 1897511141652303866L;

    private Long id;
    private String name;
    private String alias;
    private Integer ordered;
    private ElementType type;
    private Page page;
}
