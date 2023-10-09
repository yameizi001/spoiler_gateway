package com.yameizitd.gateway.spoiler.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "yameizitd.gateway.spoiler.snowflake")
public class SnowflakeProperties {
    private long workerId;
    private long datacenterId;
}
