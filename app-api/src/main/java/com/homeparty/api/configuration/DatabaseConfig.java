package com.homeparty.api.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Primary
    @Bean
    @ConfigurationProperties("homeparty.datasource.main")
    public DataSource homePartyDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }
}
