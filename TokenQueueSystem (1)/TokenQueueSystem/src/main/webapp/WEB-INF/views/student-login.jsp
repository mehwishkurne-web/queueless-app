<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Student Register / Login</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body><div class="container narrow">
  <h1 class="center">Student Portal</h1>
  <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>
  <c:if test="${not empty success}"><div class="alert ok"><c:out value="${success}"/></div></c:if>

  <div class="tabs">
    <button type="button" id="tLogin" class="tab active">Login</button>
    <button type="button" id="tReg" class="tab">Register</button>
  </div>

  <form method="post" action="${pageContext.request.contextPath}/student-auth" id="loginForm" class="panel">
    <input type="hidden" name="action" value="login">
    <label>Roll No</label><input name="rollNo" required>
    <label>Password</label><input type="password" name="password" required>
    <button class="btn" type="submit">Login</button>
  </form>

  <form method="post" action="${pageContext.request.contextPath}/student-auth" id="regForm" class="panel" hidden>
    <input type="hidden" name="action" value="register">
    <label>Full Name</label><input name="name" required>
    <label>Roll No</label><input name="rollNo" required>
    <label>Email</label><input type="email" name="email">
    <label>Password (min 4 chars)</label><input type="password" name="password" required minlength="4">
    <button class="btn" type="submit">Register</button>
  </form>
  <p class="center"><a href="${pageContext.request.contextPath}/index.jsp">← Home</a></p>
</div>
<script>
  var tl=document.getElementById('tLogin'), tr=document.getElementById('tReg'),
      lf=document.getElementById('loginForm'), rf=document.getElementById('regForm');
  function tab(reg){ lf.hidden=reg; rf.hidden=!reg; tl.classList.toggle('active',!reg); tr.classList.toggle('active',reg); }
  tl.onclick=function(){tab(false)}; tr.onclick=function(){tab(true)};
  tab('${tab}'==='register');
</script>
</body></html>
