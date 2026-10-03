<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Admin Login</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body><div class="container narrow">
  <h1 class="center">Admin Login</h1>
  <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>
  <form method="post" action="${pageContext.request.contextPath}/admin-auth" class="panel">
    <label>Username</label><input name="username" required>
    <label>Password</label><input type="password" name="password" required>
    <button class="btn" type="submit">Login</button>
  </form>
  <p class="center"><a href="${pageContext.request.contextPath}/index.jsp">← Home</a></p>
</div></body></html>
