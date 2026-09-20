package com.srihari.sriharimart.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class DBUtil {

    private DBUtil() {
        // Utility class - no objects
    }

    public static HikariDataSource createDataSource() {

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:h2:./data/srirammart");
        config.setDriverClassName("org.h2.Driver");
        config.setUsername("sa");
        config.setPassword("");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);

        config.setPoolName("SriramMartHikariPool");

        System.out.println("H2 driver class = " + org.h2.Driver.class);
        System.out.println("H2 driver classloader = " + org.h2.Driver.class.getClassLoader());
        System.out.println("DBUtil classloader = " + DBUtil.class.getClassLoader());

        return new HikariDataSource(config);
    }
}