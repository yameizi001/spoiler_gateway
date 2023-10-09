package com.yameizitd.gateway.spoiler.eventbus;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
public class AbstractEventSubscriber implements EventSubscriber {
    private final List<EventListener> genericListenerList = new ArrayList<>();
    private final Map<Class, List<EventListener>> listenerMap = new HashMap<>();

    public AbstractEventSubscriber(List<EventListener> eventListeners) {
        classify(eventListeners);
    }

    private void classify(List<EventListener> eventListeners) {
        for (EventListener eventListener : eventListeners) {
            Type[] types = eventListener.getClass().getGenericInterfaces();
            for (Type type : types) {
                if (type.getTypeName().startsWith(EventListener.class.getName())) {
                    if (type instanceof ParameterizedType) {
                        Type[] genericTypes = ((ParameterizedType) type).getActualTypeArguments();
                        if (genericTypes == null || genericTypes.length == 0) {
                            genericListenerList.add(eventListener);
                        } else {
                            Class clazz = (Class) genericTypes[0];
                            if (listenerMap.containsKey(clazz)) {
                                List<EventListener> listeners = listenerMap.get(clazz);
                                listeners.add(eventListener);
                            } else {
                                List<EventListener> listeners = new ArrayList<>();
                                listeners.add(eventListener);
                                listenerMap.put(clazz, listeners);
                            }
                        }
                    } else {
                        genericListenerList.add(eventListener);
                    }
                }
            }
        }
        log.debug("Generic listeners: {}", genericListenerList);
        log.debug("Exact listeners map: {}", listenerMap);
    }

    @Override
    public Mono<Void> subscribe(Mono<Event> event) {
        return event.flatMap(originEvent -> {
            log.debug("Receive subscribing event: {}", originEvent);
            // exact listener
            List<EventListener> listeners = this.listenerMap.get(originEvent.getClass());
            log.debug("Exact listeners: {}", listeners);
            List<Mono> publishers = new ArrayList<>();
            if (listeners != null && !listeners.isEmpty()) {
                for (EventListener listener : listeners) {
                    publishers.add(listener.onEvent(Mono.just(originEvent)));
                }
            }
            // generic listener
            log.debug("Generic listeners: {}", genericListenerList);
            for (EventListener listener : genericListenerList) {
                publishers.add(listener.onEvent(Mono.just(originEvent)));
            }
            return Flux.mergeSequential(publishers.toArray(new Mono[0]))
                    .onErrorContinue((throwable, o) -> log.error("Failed to handle event", throwable))
                    .then();
        });
    }
}
