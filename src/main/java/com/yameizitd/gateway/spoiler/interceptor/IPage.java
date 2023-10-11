package com.yameizitd.gateway.spoiler.interceptor;

import java.io.Serializable;
import java.util.List;

public interface IPage<T> extends Serializable {
    Long getNum();

    Long getSize();

    Boolean getFirst();

    Boolean getLast();

    Long getCount();

    Long getTotal();

    Long getPages();

    List<T> getRecords();

    void setRecords(List<T> records);

    static <T> IPage<T> of(long num, long size) {
        if (num <= 0)
            throw new IllegalArgumentException("Param 'pageNum' must be greater than 0");
        return new PgPage<>(num, size);
    }

    static <T> IPage<T> of(long num, long size, long total) {
        if (num <= 0)
            throw new IllegalArgumentException("Param 'pageNum' must be greater than 0");
        PgPage<T> page = new PgPage<>();
        page.init(num, size, total);
        return page;
    }
}
