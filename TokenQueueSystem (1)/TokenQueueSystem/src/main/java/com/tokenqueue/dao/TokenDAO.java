package com.tokenqueue.dao;

import com.tokenqueue.util.DBUtil;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

/** All token/queue database operations (JDBC + MySQL). */
public class TokenDAO {

    private static final String TOKEN_SELECT =
        "SELECT t.id, t.token_no, t.status, t.department_id, d.name AS dept, s.name AS service, " +
        "st.name AS student, st.roll_no, t.created_at " +
        "FROM tokens t JOIN departments d ON d.id = t.department_id " +
        "JOIN services s ON s.id = t.service_id " +
        "JOIN students st ON st.id = t.student_id ";

    private static Map<String, Object> mapToken(ResultSet rs) throws SQLException {
        Map<String, Object> m = new LinkedHashMap<>();
        int no = rs.getInt("token_no");
        m.put("id", rs.getInt("id"));
        m.put("tokenNo", no);
        m.put("tokenLabel", String.format("%03d", no));
        m.put("status", rs.getString("status"));
        m.put("departmentId", rs.getInt("department_id"));
        m.put("department", rs.getString("dept"));
        m.put("service", rs.getString("service"));
        m.put("student", rs.getString("student"));
        m.put("rollNo", rs.getString("roll_no"));
        Timestamp ts = rs.getTimestamp("created_at");
        m.put("time", ts == null ? "" : new SimpleDateFormat("hh:mm a").format(ts));
        return m;
    }

    /** Runs a query returning one int (first column of first row), 0 if none. */
    private static int scalar(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private static List<Map<String, Object>> tokenList(String where, Object... args) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(TOKEN_SELECT + where)) {
            for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapToken(rs));
            }
        }
        return list;
    }

    // ---------- Selection menus ----------
    public static List<Map<String, Object>> departments() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, name FROM departments ORDER BY name")) {
            while (rs.next()) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", rs.getInt(1));
                m.put("name", rs.getString(2));
                list.add(m);
            }
        }
        return list;
    }

    public static List<Map<String, Object>> services() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, department_id, name FROM services ORDER BY name")) {
            while (rs.next()) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", rs.getInt(1));
                m.put("departmentId", rs.getInt(2));
                m.put("name", rs.getString(3));
                list.add(m);
            }
        }
        return list;
    }

    // ---------- Student side ----------
    /** Student's token for today that is still WAITING or CALLED (or null). */
    public static Map<String, Object> activeToken(int studentId) throws SQLException {
        List<Map<String, Object>> l = tokenList(
            "WHERE t.student_id = ? AND t.token_date = CURDATE() AND t.status IN ('WAITING','CALLED') " +
            "ORDER BY t.id DESC LIMIT 1", studentId);
        return l.isEmpty() ? null : l.get(0);
    }

    public static Map<String, Object> getToken(int tokenId, int studentId) throws SQLException {
        List<Map<String, Object>> l = tokenList("WHERE t.id = ? AND t.student_id = ?", tokenId, studentId);
        return l.isEmpty() ? null : l.get(0);
    }

    /** Generate Token: next number for that department today. Returns new token id. */
    public static synchronized int generate(int studentId, int deptId, int serviceId) throws SQLException {
        try (Connection c = DBUtil.getConnection()) {
            if (scalar(c, "SELECT COUNT(*) FROM services WHERE id = ? AND department_id = ?",
                       serviceId, deptId) == 0) {
                throw new IllegalArgumentException("Invalid service for department");
            }
            int next = scalar(c, "SELECT COALESCE(MAX(token_no),0)+1 FROM tokens " +
                                 "WHERE department_id = ? AND token_date = CURDATE()", deptId);
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO tokens (token_no, student_id, department_id, service_id, status, token_date) " +
                    "VALUES (?, ?, ?, ?, 'WAITING', CURDATE())", Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, next);
                ps.setInt(2, studentId);
                ps.setInt(3, deptId);
                ps.setInt(4, serviceId);
                ps.executeUpdate();
                try (ResultSet k = ps.getGeneratedKeys()) {
                    k.next();
                    return k.getInt(1);
                }
            }
        }
    }

    /** Check Queue Status: token info + people ahead + token now being served. */
    public static Map<String, Object> queueStatus(int tokenId, int studentId) throws SQLException {
        Map<String, Object> t = getToken(tokenId, studentId);
        if (t == null) return null;
        int deptId = (Integer) t.get("departmentId");
        int no = (Integer) t.get("tokenNo");
        try (Connection c = DBUtil.getConnection()) {
            int ahead = scalar(c, "SELECT COUNT(*) FROM tokens WHERE department_id = ? " +
                "AND token_date = CURDATE() AND status = 'WAITING' AND token_no < ?", deptId, no);
            int serving = scalar(c, "SELECT token_no FROM tokens WHERE department_id = ? " +
                "AND token_date = CURDATE() AND status = 'CALLED' LIMIT 1", deptId);
            t.put("ahead", ahead);
            t.put("nowServing", serving == 0 ? "---" : String.format("%03d", serving));
        }
        return t;
    }

    // ---------- Admin side ----------
    public static List<Map<String, Object>> queue(int deptId) throws SQLException {
        return tokenList("WHERE t.department_id = ? AND t.token_date = CURDATE() " +
                         "AND t.status IN ('WAITING','CALLED') ORDER BY t.token_no", deptId);
    }

    public static Map<String, Object> currentToken(int deptId) throws SQLException {
        List<Map<String, Object>> l = tokenList("WHERE t.department_id = ? AND t.token_date = CURDATE() " +
                                                "AND t.status = 'CALLED' LIMIT 1", deptId);
        return l.isEmpty() ? null : l.get(0);
    }

    public static int countToday(int deptId, String status) throws SQLException {
        try (Connection c = DBUtil.getConnection()) {
            return scalar(c, "SELECT COUNT(*) FROM tokens WHERE department_id = ? " +
                             "AND token_date = CURDATE() AND status = ?", deptId, status);
        }
    }

    /** Call Next Token. Returns 1 = called, 0 = queue empty, 2 = a token is still being served. */
    public static synchronized int callNext(int deptId) throws SQLException {
        try (Connection c = DBUtil.getConnection()) {
            if (scalar(c, "SELECT COUNT(*) FROM tokens WHERE department_id = ? " +
                          "AND token_date = CURDATE() AND status = 'CALLED'", deptId) > 0) return 2;
            int id = scalar(c, "SELECT id FROM tokens WHERE department_id = ? AND token_date = CURDATE() " +
                               "AND status = 'WAITING' ORDER BY token_no LIMIT 1", deptId);
            if (id == 0) return 0;
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE tokens SET status = 'CALLED', called_at = NOW() WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            return 1;
        }
    }

    /** Mark Token Completed. */
    public static boolean complete(int tokenId, int deptId) throws SQLException {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "UPDATE tokens SET status = 'COMPLETED', completed_at = NOW() " +
                 "WHERE id = ? AND department_id = ? AND status = 'CALLED'")) {
            ps.setInt(1, tokenId);
            ps.setInt(2, deptId);
            return ps.executeUpdate() > 0;
        }
    }
}
