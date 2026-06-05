package com.aistudy.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnection {
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
            } else {
                prop.load(input);
                URL = getConfigValue("DB_URL", prop.getProperty("db.url"));
                USER = getConfigValue("DB_USERNAME", prop.getProperty("db.username"));
                PASSWORD = getConfigValue("DB_PASSWORD", prop.getProperty("db.password"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String getConfigValue(String envName, String fallback) {
        String value = System.getenv(envName);
        return value == null || value.isBlank() ? fallback : value;
    }

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
