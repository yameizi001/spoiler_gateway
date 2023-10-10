package com.yameizitd.gateway.spoiler.eventbus;

import lombok.*;
import org.springframework.cloud.client.ServiceInstance;

import java.io.Serial;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class InstanceRefreshEvent extends RefreshEvent {
    @Serial
    private static final long serialVersionUID = -5616605922579477278L;

    private final List<ServiceInstance> serviceInstances;

    public InstanceRefreshEvent(RefreshEvent.Operation operation, List<ServiceInstance> serviceInstances) {
        super(operation);
        this.serviceInstances = serviceInstances;
    }
}
