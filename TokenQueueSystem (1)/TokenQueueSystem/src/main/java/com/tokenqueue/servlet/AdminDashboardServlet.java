package com.tokenqueue.servlet;

import com.tokenqueue.dao.TokenDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Admin Dashboard: View Token List, Call Next Token, Display Current Token, Mark Completed. */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private static int parse(String v, int def) {
        try { return Integer.parseInt(v); } catch (Exception e) { return def; }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession s = req.getSession();
        try {
            List<Map<String, Object>> depts = TokenDAO.departments();
            int def = depts.isEmpty() ? 0 : (Integer) depts.get(0).get("id");
            int deptId = parse(req.getParameter("deptId"), def);
            req.setAttribute("departments", depts);
            req.setAttribute("deptId", deptId);
            req.setAttribute("queue", TokenDAO.queue(deptId));
            req.setAttribute("current", TokenDAO.currentToken(deptId));
            req.setAttribute("waitingCount", TokenDAO.countToday(deptId, "WAITING"));
            req.setAttribute("completedCount", TokenDAO.countToday(deptId, "COMPLETED"));
            Object flash = s.getAttribute("flash");
            if (flash != null) {
                req.setAttribute("flash", flash);
                s.removeAttribute("flash");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession s = req.getSession();
        int deptId = parse(req.getParameter("deptId"), 0);
        String action = req.getParameter("action");
        try {
            if ("next".equals(action)) {
                int r = TokenDAO.callNext(deptId);
                s.setAttribute("flash", r == 1 ? "Next token called." :
                                        r == 0 ? "No tokens waiting in the queue." :
                                                 "Complete the current token before calling the next one.");
            } else if ("complete".equals(action)) {
                boolean ok = TokenDAO.complete(parse(req.getParameter("tokenId"), 0), deptId);
                s.setAttribute("flash", ok ? "Token marked as completed." : "Nothing to complete.");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/dashboard?deptId=" + deptId);
    }
}
