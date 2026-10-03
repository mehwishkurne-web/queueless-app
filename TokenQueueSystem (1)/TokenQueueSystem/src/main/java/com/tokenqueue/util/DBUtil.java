package com.tokenqueue.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** JDBC connection helper. Reads settings from db.properties. */
public class DBUtil {
    private static String url, user, password;

    static {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties p = new Properties();
            p.load(in);
            url = p.getProperty("url");
            user = p.getProperty("user");
            password = p.getProperty("password");
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
