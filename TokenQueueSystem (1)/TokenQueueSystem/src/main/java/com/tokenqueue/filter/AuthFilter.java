package com.tokenqueue.filter;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

/** Protects /student/* and /admin/* pages. */
@WebFilter(urlPatterns = {"/student/*", "/admin/*"})
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) rq;
        HttpServletResponse resp = (HttpServletResponse) rs;
        HttpSession s = req.getSession(false);
        String path = req.getServletPath();
        boolean ok;
        String login;
        if (path.startsWith("/admin")) {
            ok = s != null && s.getAttribute("adminId") != null;
            login = "/admin-auth";
        } else {
            ok = s != null && s.getAttribute("studentId") != null;
            login = "/student-auth";
        }
        if (!ok) {
            resp.sendRedirect(req.getContextPath() + login);
            return;
        }
        resp.setHeader("Cache-Control", "no-store");
        chain.doFilter(rq, rs);
    }
}
