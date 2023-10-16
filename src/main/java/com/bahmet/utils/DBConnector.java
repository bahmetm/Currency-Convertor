package com.bahmet.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnector {
    private static final String url;
    private static final String user;
    private static final String password;

    static {
        try {
            InputStream input = DBConnector.class.getClassLoader().getResourceAsStream("config.properties");
            Properties properties = new Properties();
            properties.load(input);
            url = properties.getProperty("db.url");
            user = properties.getProperty("db.user");
            password = properties.getProperty("db.password");

//            Class.forName("org.postgres.Driver");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize DBConnector", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
