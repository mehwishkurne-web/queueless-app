package com.tokenqueue.servlet;

import com.tokenqueue.util.DBUtil;
import com.tokenqueue.util.PasswordUtil;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Student Register / Login / Logout. */
@WebServlet("/student-auth")
public class StudentAuthServlet extends HttpServlet {

    private void show(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/student-login.jsp").forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession s = req.getSession(false);
        if ("logout".equals(req.getParameter("action"))) {
            if (s != null) s.invalidate();
            resp.sendRedirect(req.getContextPath() + "/student-auth");
            return;
        }
        if (s != null && s.getAttribute("studentId") != null) {
            resp.sendRedirect(req.getContextPath() + "/student/dashboard");
            return;
        }
        show(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        try {
            if ("register".equals(action)) register(req, resp);
            else login(req, resp);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Database error. Check MySQL is running and db.properties is correct.");
            show(req, resp);
        }
    }

    private void register(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        String name = trim(req.getParameter("name"));
        String roll = trim(req.getParameter("rollNo"));
        String email = trim(req.getParameter("email"));
        String pwd = req.getParameter("password");
        req.setAttribute("tab", "register");
        if (name.isEmpty() || roll.isEmpty() || pwd == null || pwd.length() < 4) {
            req.setAttribute("error", "Name, roll number and a password (min 4 chars) are required.");
            show(req, resp);
            return;
        }
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "INSERT INTO students (name, roll_no, email, password_hash) VALUES (?,?,?,?)")) {
            ps.setString(1, name);
            ps.setString(2, roll);
            ps.setString(3, email);
            ps.setString(4, PasswordUtil.hash(pwd));
            ps.executeUpdate();
            req.setAttribute("tab", "login");
            req.setAttribute("success", "Registration successful. Please login.");
        } catch (SQLIntegrityConstraintViolationException dup) {
            req.setAttribute("error", "This roll number is already registered.");
        }
        show(req, resp);
    }

    private void login(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        String roll = trim(req.getParameter("rollNo"));
        String pwd = req.getParameter("password") == null ? "" : req.getParameter("password");
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT id, name FROM students WHERE roll_no = ? AND password_hash = ?")) {
            ps.setString(1, roll);
            ps.setString(2, PasswordUtil.hash(pwd));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HttpSession old = req.getSession(false);
                    if (old != null) old.invalidate();
                    HttpSession s = req.getSession(true);
                    s.setAttribute("studentId", rs.getInt("id"));
                    s.setAttribute("studentName", rs.getString("name"));
                    resp.sendRedirect(req.getContextPath() + "/student/dashboard");
                    return;
                }
            }
        }
        req.setAttribute("error", "Invalid roll number or password.");
        show(req, resp);
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }
}
