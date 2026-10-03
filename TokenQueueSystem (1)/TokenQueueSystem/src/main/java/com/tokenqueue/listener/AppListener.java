package com.tokenqueue.listener;

import com.tokenqueue.util.DBUtil;
import com.tokenqueue.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/** Creates the default admin (admin / admin123) if no admin exists. */
@WebListener
public class AppListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Connection c = DBUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM admins")) {
            rs.next();
            if (rs.getInt(1) == 0) {
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO admins (username, password_hash) VALUES (?, ?)")) {
                    ps.setString(1, "admin");
                    ps.setString(2, PasswordUtil.hash("admin123"));
                    ps.executeUpdate();
                    System.out.println("[TokenQueue] Default admin created: admin / admin123");
                }
            }
        } catch (Exception e) {
            System.err.println("[TokenQueue] DB check failed. Is MySQL running and schema.sql imported?");
            e.printStackTrace();
        }
    }
}
