package com.yameizitd.gateway.spoiler.util;

import com.yameizitd.gateway.spoiler.interceptor.IPage;

import java.util.function.Function;
import java.util.stream.Collectors;

public final class PageUtils {
    public static <T, U> IPage<U> map(IPage<T> page, Function<T, U> mapper) {
        IPage<U> pager = IPage.of(page.getNum(), page.getSize(), page.getTotal());
        pager.setRecords(page.getRecords()
                .stream()
                .map(mapper)
                .collect(Collectors.toList()));
        return pager;
    }
}
