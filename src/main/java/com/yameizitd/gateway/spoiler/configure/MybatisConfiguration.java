package com.yameizitd.gateway.spoiler.configure;

import com.yameizitd.gateway.spoiler.interceptor.PgPageInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisConfiguration {
    @Bean
    public PgPageInterceptor pageInterceptor() {
        return new PgPageInterceptor();
    }
}
