package com.example.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    // Oracle (Primary JPA)
    @Value("${spring.datasource.url}")
    private String oracleUrl;

    @Value("${spring.datasource.username}")
    private String oracleUser;

    @Value("${spring.datasource.password}")
    private String oraclePass;

    @Value("${spring.datasource.driver-class-name}")
    private String oracleDriver;

    @Bean(name = "oracleDataSource")
    @Primary
    public DataSource oracleDataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl(oracleUrl);
        ds.setUsername(oracleUser);
        ds.setPassword(oraclePass);
        ds.setDriverClassName(oracleDriver);
        return ds;
    }

    // H2 (Optional)
    @Bean(name = "h2DataSource")
    public DataSource h2DataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        ds.setUsername("sa");
        ds.setPassword("");
        ds.setDriverClassName("org.h2.Driver");
        return ds;
    }

    // JdbcTemplate for H2
    @Bean(name = "h2JdbcTemplate")
    public JdbcTemplate h2JdbcTemplate(@Qualifier( "h2DataSource") DataSource h2DataSource) {
        return new JdbcTemplate(h2DataSource);
    }
}
