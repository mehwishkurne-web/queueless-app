package com.tokenqueue.servlet;

import com.tokenqueue.dao.TokenDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** POST = Generate Token, GET = Display Token Number. */
@WebServlet("/student/token")
public class StudentTokenServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession s = req.getSession();
        int sid = (Integer) s.getAttribute("studentId");
        try {
            Map<String, Object> t = TokenDAO.activeToken(sid);
            if (t == null) {
                resp.sendRedirect(req.getContextPath() + "/student/dashboard");
                return;
            }
            s.setAttribute("tokenId", t.get("id"));
            req.setAttribute("token", t);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher("/WEB-INF/views/student-token.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int sid = (Integer) req.getSession().getAttribute("studentId");
        try {
            if (TokenDAO.activeToken(sid) == null) {
                int dept = Integer.parseInt(req.getParameter("departmentId"));
                int svc = Integer.parseInt(req.getParameter("serviceId"));
                TokenDAO.generate(sid, dept, svc);
            }
            resp.sendRedirect(req.getContextPath() + "/student/token");
        } catch (IllegalArgumentException e) {
            resp.sendRedirect(req.getContextPath() + "/student/dashboard");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
