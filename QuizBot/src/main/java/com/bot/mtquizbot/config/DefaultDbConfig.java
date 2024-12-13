package com.bot.mtquizbot.config;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;

class DefaultDbConfig {
    protected DataSource hikariDataSource(String tag, DbConfig.SpringDataJdbcProperties properties) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(properties.getUrl());
        ds.setDriverClassName(properties.getDriver());
        ds.setUsername(properties.getUser());
        ds.setPassword(properties.getPassword());
        ds.setMaximumPoolSize(Integer.parseInt(properties.getPoolSize()));
        return ds;
    }
}