package com.payments.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL  = "jdbc:postgresql://localhost:5434/sridemo";
    private static final String USER = "postgres";
    private static final String PASS = "root";

    // Always get a new connection when called
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
