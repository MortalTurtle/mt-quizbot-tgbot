package com.bot.mtquizbot.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Configuration
public class DbConfig extends DefaultDbConfig {

    @Bean
    @Qualifier("bot-db")
    @ConfigurationProperties(prefix = "app.db.bot-db")
    SpringDataJdbcProperties gitlabJdbcProperties() {
        return new SpringDataJdbcProperties();
    }

    @Bean
    @Qualifier("bot-db")
    public DataSource gitlabDataSource(@Qualifier("bot-db") SpringDataJdbcProperties properties) {
        return hikariDataSource("db", properties);
    }

    @Bean
    @Qualifier("bot-db")
    JdbcTemplate gitlabJdbcTemplate(@Qualifier("bot-db") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Data
    @NoArgsConstructor
    public static class SpringDataJdbcProperties {

        private static final String H2_DATABASE_DRIVER = "org.h2.Driver";

        String url;
        String driver;
        String user;
        String password;
        String poolSize;
        int minPoolSize = 4;
        int maxPoolSize = 10;
        long idleTimeout;
        long maxLifetime;
        Integer bulkSize;

        /**
         * All-args constructor for {@link SpringDataJdbcProperties#toString()}
         * (logging)
         *
         * @param url      JDBC driver class name property
         * @param driver   JDBC driver class name property
         * @param user     JDBC username property
         * @param password JDBC password property
         * @param poolSize Hikari / Vertica maxPoolSize property
         * @param bulkSize bulk insert size
         */
        public SpringDataJdbcProperties(
                String url, String driver, String user, String password, String poolSize, Integer bulkSize) {
            this.url = url;
            this.driver = driver;
            this.user = user;
            this.password = password;
            this.poolSize = poolSize;
            this.bulkSize = bulkSize;
        }

        public boolean isH2Database() {
            return driver.equals(H2_DATABASE_DRIVER);
        }

        @Override
        public String toString() {
            var props = new SpringDataJdbcProperties(
                    url, driver, user, ((password == null) || password.isEmpty()) ? "" : "*****", poolSize, bulkSize);
            return Json.encode(props);
        }

    }

}