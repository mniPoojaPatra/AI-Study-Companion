package com.aistudy.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

@WebListener
public class SchemaInitializer implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (InputStream input = SchemaInitializer.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (input == null) {
                throw new IllegalStateException("schema.sql was not found on the classpath.");
            }

            String schema = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            try (Connection connection = DBConnection.getConnection();
                 Statement statement = connection.createStatement()) {
                for (String sql : schema.split(";")) {
                    String trimmedSql = sql.trim();
                    if (!trimmedSql.isEmpty()) {
                        statement.execute(trimmedSql);
                    }
                }
            }
        } catch (Exception ex) {
            sce.getServletContext().log("Database schema initialization failed.", ex);
        }
    }
}
