package com.yameizitd.gateway.spoiler.eventbus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.cloud.client.ServiceInstance;

import java.io.Serial;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class InstanceRefreshEvent extends RefreshEvent {
    @Serial
    private static final long serialVersionUID = -5616605922579477278L;

    private List<ServiceInstance> serviceInstances;

    public InstanceRefreshEvent(Operation operation, List<ServiceInstance> serviceInstances) {
        super(operation);
        this.serviceInstances = serviceInstances;
    }
}
