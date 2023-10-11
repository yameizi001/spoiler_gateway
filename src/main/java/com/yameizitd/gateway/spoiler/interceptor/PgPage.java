package com.yameizitd.gateway.spoiler.interceptor;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.List;

@Data
@NoArgsConstructor
public class PgPage<T> implements IPage<T> {
    @Serial
    private static final long serialVersionUID = 3233196011164431276L;

    private Long num;
    private Long size;
    private Boolean first;
    private Boolean last;
    private Long count;
    private Long total;
    private Long pages;
    private List<T> records;

    public PgPage(long num, long size) {
        this.num = num;
        this.size = size;
    }

    public PgPage(long num, long size, long total, List<T> records) {
        init(num, size, total);
        this.count = records != null ? records.size() : 0L;
        this.records = records;
    }

    public void init(long num, long size, long total) {
        this.pages = total % size == 0 ? total / size : total / size + 1;
        this.num = num;
        this.size = size;
        this.first = num == 1;
        this.last = num == pages;
        this.total = total;
    }

    public void setRecords(List<T> records) {
        this.count = this.count == null ? (records != null ? records.size() : 0L) : this.count;
        this.records = records;
    }
}
