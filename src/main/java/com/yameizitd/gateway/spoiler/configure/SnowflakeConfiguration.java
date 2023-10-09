package com.yameizitd.gateway.spoiler.configure;

import com.yameizitd.gateway.spoiler.property.SnowflakeProperties;
import com.yameizitd.gateway.spoiler.util.IdUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SnowflakeProperties.class)
public class SnowflakeConfiguration {
    @Bean
    public IdUtils idUtils(SnowflakeProperties snowflakeProperties) {
        return new IdUtils(snowflakeProperties);
    }
}
