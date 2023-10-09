package com.yameizitd.gateway.spoiler.eventbus.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yameizitd.gateway.spoiler.eventbus.AbstractEventSubscriber;
import com.yameizitd.gateway.spoiler.eventbus.Event;
import com.yameizitd.gateway.spoiler.eventbus.EventListener;
import com.yameizitd.gateway.spoiler.util.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@SuppressWarnings("rawtypes")
@Slf4j
public class RedisEventSubscriber extends AbstractEventSubscriber implements InitializingBean, DisposableBean {
    private final ObjectMapper mapper = JacksonUtils.newInstance();
    private final ReactiveRedisMessageListenerContainer redisMessageListenerContainer;
    private Disposable redisMessageListenerDisposable;

    public RedisEventSubscriber(ReactiveRedisMessageListenerContainer redisMessageListenerContainer,
                                List<EventListener> eventListeners) {
        super(eventListeners);
        this.redisMessageListenerContainer = redisMessageListenerContainer;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        redisMessageListenerDisposable = redisMessageListenerContainer.receive(ChannelTopic.of(RedisEventConstants.REDIS_EVENT_BUS_TOPIC))
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(message -> {
                    String body = message.getMessage();
                    try {
                        log.debug("Receive message: {}", body);
                        Event event = mapper.readValue(body, Event.class);
                        this.subscribe(Mono.just(event))
                                .onErrorResume(throwable -> {
                                    // ignore
                                    log.error("Failed to handle event", throwable);
                                    return Mono.empty();
                                })
                                .subscribe();
                    } catch (Exception e) {
                        // ignore
                        log.error("Ignore, failed to deserialize message to event", e);
                    }
                })
                .subscribe();
    }

    @Override
    public void destroy() throws Exception {
        redisMessageListenerDisposable.dispose();
    }
}
