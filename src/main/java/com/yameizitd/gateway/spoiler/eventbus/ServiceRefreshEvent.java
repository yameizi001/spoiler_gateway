package com.yameizitd.gateway.spoiler.eventbus;

import com.yameizitd.gateway.spoiler.domain.business.ServiceDefinition;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class ServiceRefreshEvent extends RefreshEvent {
    @Serial
    private static final long serialVersionUID = 952686642739342296L;

    private final List<ServiceDefinition> serviceDefinitions;

    public ServiceRefreshEvent(RefreshEvent.Operation operation, List<ServiceDefinition> serviceDefinitions) {
        super(operation);
        this.serviceDefinitions = serviceDefinitions;
    }
}
