package com.yameizitd.gateway.spoiler.eventbus;

import reactor.core.publisher.Mono;

public interface EventListener<T extends Event> {
    Mono<Void> onEvent(Mono<T> event);
}
