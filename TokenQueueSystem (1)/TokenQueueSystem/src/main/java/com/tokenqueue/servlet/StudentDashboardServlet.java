package com.tokenqueue.servlet;

import com.tokenqueue.dao.TokenDAO;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Student Dashboard -> Select Department -> Select Service. */
@WebServlet("/student/dashboard")
public class StudentDashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int sid = (Integer) req.getSession().getAttribute("studentId");
        try {
            if (TokenDAO.activeToken(sid) != null) {   // one active token at a time
                resp.sendRedirect(req.getContextPath() + "/student/token");
                return;
            }
            req.setAttribute("departments", TokenDAO.departments());
            req.setAttribute("services", TokenDAO.services());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher("/WEB-INF/views/student-dashboard.jsp").forward(req, resp);
    }
}
