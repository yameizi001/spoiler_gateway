package com.yameizitd.gateway.spoiler.handler;

import com.yameizitd.gateway.spoiler.eventbus.RefreshEvent;

import java.util.List;

public interface EventHandler {
    <T> void publish(RefreshEvent.Operation operation, T entity);

    void publish(RefreshEvent.Operation operation, List<?> entities);
}
