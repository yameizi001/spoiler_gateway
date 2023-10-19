package com.yameizitd.gateway.spoiler.discovery;

import com.yameizitd.gateway.spoiler.domain.entity.InstanceEntity;
import com.yameizitd.gateway.spoiler.domain.entity.ServiceEntity;
import com.yameizitd.gateway.spoiler.domain.form.ServiceQueryForm;
import com.yameizitd.gateway.spoiler.interceptor.IPage;
import com.yameizitd.gateway.spoiler.mapper.InstanceMapper;
import com.yameizitd.gateway.spoiler.mapper.ServiceMapper;
import com.yameizitd.gateway.spoiler.mapstruct.InstanceMapstruct;
import org.springframework.cloud.client.ServiceInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SysDiscoveryClientLoader {
    private static final long START_PAGE_NUM = 1;
    private static final long PAGE_SIZE = 100;
    private final Map<String, List<ServiceInstance>> cache = new HashMap<>(256);
    private final ServiceMapper serviceMapper;
    private final InstanceMapper instanceMapper;
    private final InstanceMapstruct instanceMapstruct;

    public SysDiscoveryClientLoader(ServiceMapper serviceMapper,
                                    InstanceMapper instanceMapper,
                                    InstanceMapstruct instanceMapstruct) {
        this.serviceMapper = serviceMapper;
        this.instanceMapper = instanceMapper;
        this.instanceMapstruct = instanceMapstruct;
    }

    public Map<String, List<ServiceInstance>> loadedServiceInstances() {
        return cache;
    }

    public Map<String, List<ServiceInstance>> refresh() {
        cache.clear();
        load(START_PAGE_NUM);
        return cache;
    }

    private void load(long num) {
        List<ServiceEntity> serviceRecords = serviceMapper.selectByOptions(
                new ServiceQueryForm(null, null, true, null), IPage.of(num, PAGE_SIZE)
        );
        if (serviceRecords == null || serviceRecords.isEmpty()) {
            return;
        }
        for (ServiceEntity service : serviceRecords) {
            Long serviceId = service.getId();
            List<ServiceInstance> instances = new ArrayList<>();
            List<InstanceEntity> instanceRecords = instanceMapper.selectEnabledByServiceId(serviceId);
            if (instanceRecords != null && !instanceRecords.isEmpty()) {
                List<ServiceInstance> serviceInstances = instanceRecords.stream()
                        .map(instanceMapstruct::entity2definition)
                        .toList();
                instances.addAll(serviceInstances);
            }
            cache.put(String.valueOf(serviceId), instances);
        }
        load(++num);
    }
}
