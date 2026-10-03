<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="refresh" content="15">
<title>Admin Dashboard</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body>
<div class="topbar">
  <span>🛠️ Admin: <c:out value="${sessionScope.adminName}"/></span>
  <a href="${pageContext.request.contextPath}/admin-auth?action=logout">Logout</a>
</div>
<div class="container">

  <c:if test="${not empty flash}"><div class="alert ok"><c:out value="${flash}"/></div></c:if>

  <form method="get" action="${pageContext.request.contextPath}/admin/dashboard">
    <label>Department counter</label>
    <select name="deptId" onchange="this.form.submit()">
      <c:forEach var="d" items="${departments}">
        <option value="${d.id}" ${d.id == deptId ? 'selected' : ''}><c:out value="${d.name}"/></option>
      </c:forEach>
    </select>
  </form>

  <div class="box center">
    <div>CURRENT TOKEN</div>
    <c:choose>
      <c:when test="${not empty current}">
        <div class="big">${current.tokenLabel}</div>
        <p>Student: <b><c:out value="${current.student}"/></b> (<c:out value="${current.rollNo}"/>)
           &middot; <c:out value="${current.service}"/></p>
        <form method="post" action="${pageContext.request.contextPath}/admin/dashboard">
          <input type="hidden" name="action" value="complete">
          <input type="hidden" name="deptId" value="${deptId}">
          <input type="hidden" name="tokenId" value="${current.id}">
          <button class="btn green" type="submit">✔ Mark Token Completed</button>
        </form>
      </c:when>
      <c:otherwise>
        <div class="big dim">---</div>
        <p>No token is being served.</p>
      </c:otherwise>
    </c:choose>

    <form method="post" action="${pageContext.request.contextPath}/admin/dashboard">
      <input type="hidden" name="action" value="next">
      <input type="hidden" name="deptId" value="${deptId}">
      <button class="btn" type="submit" ${not empty current or waitingCount == 0 ? 'disabled' : ''}>📣 Call Next Token</button>
    </form>
  </div>

  <div class="row">
    <div>Waiting<b>${waitingCount}</b></div>
    <div>Completed Today<b>${completedCount}</b></div>
  </div>

  <div class="box">
    <h3>Token List (today)</h3>
    <table>
      <tr><th>Token</th><th>Student</th><th>Roll No</th><th>Service</th><th>Time</th><th>Status</th></tr>
      <c:forEach var="t" items="${queue}">
        <tr>
          <td><b>${t.tokenLabel}</b></td>
          <td><c:out value="${t.student}"/></td>
          <td><c:out value="${t.rollNo}"/></td>
          <td><c:out value="${t.service}"/></td>
          <td>${t.time}</td>
          <td><span class="badge ${t.status}">${t.status}</span></td>
        </tr>
      </c:forEach>
      <c:if test="${empty queue}"><tr><td colspan="6">Queue is empty.</td></tr></c:if>
    </table>
  </div>
</div>
</body></html>
