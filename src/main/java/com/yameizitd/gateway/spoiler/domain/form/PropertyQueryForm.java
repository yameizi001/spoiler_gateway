package com.yameizitd.gateway.spoiler.domain.form;

import com.yameizitd.gateway.spoiler.domain.Page;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PropertyQueryForm implements Serializable {
    @Serial
    private static final long serialVersionUID = 4512214977442479169L;

    private Long id;
    private Long elementId;
    private String alias;
    private String key;
    private Boolean required;
    private Page page;
}
