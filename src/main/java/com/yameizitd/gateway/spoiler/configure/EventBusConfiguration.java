package com.yameizitd.gateway.spoiler.configure;

import com.yameizitd.gateway.spoiler.eventbus.EventListener;
import com.yameizitd.gateway.spoiler.eventbus.EventPublisher;
import com.yameizitd.gateway.spoiler.eventbus.EventSubscriber;
import com.yameizitd.gateway.spoiler.eventbus.impl.RedisEventPublisher;
import com.yameizitd.gateway.spoiler.eventbus.impl.RedisEventSubscriber;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;

import java.util.List;

@SuppressWarnings("rawtypes")
@Configuration
public class EventBusConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public EventPublisher eventPublisher(ReactiveStringRedisTemplate redisTemplate) {
        return new RedisEventPublisher(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public ReactiveRedisMessageListenerContainer reactiveRedisMessageListenerContainer(
            ReactiveRedisConnectionFactory connectionFactory) {
        return new ReactiveRedisMessageListenerContainer(connectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public EventSubscriber eventSubscriber(ReactiveRedisMessageListenerContainer redisMessageListenerContainer,
                                           List<EventListener> eventListeners) {
        return new RedisEventSubscriber(redisMessageListenerContainer, eventListeners);
    }
}
