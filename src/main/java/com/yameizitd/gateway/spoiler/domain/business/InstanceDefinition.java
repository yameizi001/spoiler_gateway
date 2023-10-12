package com.yameizitd.gateway.spoiler.domain.business;

import lombok.*;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.net.URI;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class InstanceDefinition implements ServiceInstance, Serializable {
    @Serial
    private static final long serialVersionUID = -2048795876226556305L;

    public static final String INSTANCE_WEIGHT = "instance.weight";
    public static final String INSTANCE_HEALTH = "instance.health";

    private String instanceId;
    private String serviceId;
    private String scheme;
    private boolean secure;
    private String host;
    private int port;
    private Map<String, String> metadata;

    @Override
    public URI getUri() {
        return getUri(this);
    }

    private URI getUri(ServiceInstance instance) {
        String scheme;
        if (StringUtils.hasText(getScheme())) {
            scheme = getScheme();
        } else {
            scheme = instance.isSecure() ? "https" : "http";
        }
        int port = instance.getPort();
        if (port <= 0) {
            port = instance.isSecure() ? 443 : 80;
        }
        String uri = String.format("%s://%s:%s", scheme, instance.getHost(), port);
        return URI.create(uri);
    }
}
