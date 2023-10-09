package com.yameizitd.gateway.spoiler.eventbus;

import reactor.core.publisher.Mono;

public interface EventPublisher {
    Mono<Void> publish(Mono<Event> event);
}
