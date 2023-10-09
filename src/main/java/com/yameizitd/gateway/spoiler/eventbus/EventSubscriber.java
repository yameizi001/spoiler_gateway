package com.yameizitd.gateway.spoiler.eventbus;

import reactor.core.publisher.Mono;

public interface EventSubscriber {
    Mono<Void> subscribe(Mono<Event> event);
}
