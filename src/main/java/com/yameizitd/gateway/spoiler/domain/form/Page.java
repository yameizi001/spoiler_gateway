package com.yameizitd.gateway.spoiler.domain.form;

import com.yameizitd.gateway.spoiler.interceptor.IPage;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Page implements Serializable {
    @Serial
    private static final long serialVersionUID = 3558026741476035532L;

    private long num = 1;
    private long size = 20;

    public <T> IPage<T> iPage() {
        return IPage.of(num, size);
    }
}
