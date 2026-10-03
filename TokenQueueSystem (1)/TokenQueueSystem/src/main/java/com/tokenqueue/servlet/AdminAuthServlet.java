package com.tokenqueue.servlet;

import com.tokenqueue.util.DBUtil;
import com.tokenqueue.util.PasswordUtil;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Admin Login / Logout. */
@WebServlet("/admin-auth")
public class AdminAuthServlet extends HttpServlet {

    private void show(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/admin-login.jsp").forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession s = req.getSession(false);
        if ("logout".equals(req.getParameter("action"))) {
            if (s != null) s.invalidate();
            resp.sendRedirect(req.getContextPath() + "/admin-auth");
            return;
        }
        if (s != null && s.getAttribute("adminId") != null) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            return;
        }
        show(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String user = req.getParameter("username") == null ? "" : req.getParameter("username").trim();
        String pwd = req.getParameter("password") == null ? "" : req.getParameter("password");
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT id, username FROM admins WHERE username = ? AND password_hash = ?")) {
            ps.setString(1, user);
            ps.setString(2, PasswordUtil.hash(pwd));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HttpSession old = req.getSession(false);
                    if (old != null) old.invalidate();
                    HttpSession s = req.getSession(true);
                    s.setAttribute("adminId", rs.getInt("id"));
                    s.setAttribute("adminName", rs.getString("username"));
                    resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
                    return;
                }
            }
            req.setAttribute("error", "Invalid username or password.");
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Database error. Check MySQL is running and db.properties is correct.");
        }
        show(req, resp);
    }
}
