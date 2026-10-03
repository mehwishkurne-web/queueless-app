package com.tokenqueue.servlet;

import com.tokenqueue.dao.TokenDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Check Queue Status - JSON endpoint polled by the token page (Wait/Refresh -> Check Again). */
@WebServlet("/student/status")
public class StudentStatusServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession s = req.getSession();
        Integer sid = (Integer) s.getAttribute("studentId");
        Integer tid = (Integer) s.getAttribute("tokenId");
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Cache-Control", "no-store");
        try {
            Map<String, Object> t = (tid == null) ? null : TokenDAO.queueStatus(tid, sid);
            if (t == null) {
                resp.getWriter().write("{\"status\":\"NONE\"}");
                return;
            }
            resp.getWriter().write("{\"status\":\"" + t.get("status") + "\","
                + "\"tokenLabel\":\"" + t.get("tokenLabel") + "\","
                + "\"ahead\":" + t.get("ahead") + ","
                + "\"nowServing\":\"" + t.get("nowServing") + "\"}");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
