package com.yameizitd.gateway.spoiler.eventbus.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yameizitd.gateway.spoiler.eventbus.Event;
import com.yameizitd.gateway.spoiler.eventbus.EventPublisher;
import com.yameizitd.gateway.spoiler.exception.impl.TypeConvertException;
import com.yameizitd.gateway.spoiler.util.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class RedisEventPublisher implements EventPublisher {
    private final ObjectMapper mapper = JacksonUtils.newInstance();
    private final ReactiveStringRedisTemplate redisTemplate;

    public RedisEventPublisher(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> publish(Mono<Event> event) {
        return event.flatMap(originEvent ->
                        // block method
                        Mono.fromCallable(() -> {
                            try {
                                log.debug("Publishing refresh event: {}", originEvent);
                                return mapper.writeValueAsString(originEvent);
                            } catch (Exception exception) {
                                throw new TypeConvertException(
                                        "An error occurred during serializing the event instance",
                                        exception
                                );
                            }
                        }))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(message -> {
                    log.debug("Publish message: {}", message);
                    return redisTemplate.convertAndSend(RedisEventConstants.REDIS_EVENT_BUS_TOPIC, message);
                })
                .then();
    }
}
