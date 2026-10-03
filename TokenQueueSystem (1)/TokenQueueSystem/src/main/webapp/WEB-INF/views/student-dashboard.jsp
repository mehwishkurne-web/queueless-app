<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Student Dashboard</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body>
<div class="topbar">
  <span>👋 Welcome, <c:out value="${sessionScope.studentName}"/></span>
  <a href="${pageContext.request.contextPath}/student-auth?action=logout">Logout</a>
</div>
<div class="container">
  <form method="post" action="${pageContext.request.contextPath}/student/token">
    <input type="hidden" name="departmentId" id="departmentId">
    <input type="hidden" name="serviceId" id="serviceId">

    <h2>1. Select Department</h2>
    <div class="grid">
      <c:forEach var="d" items="${departments}">
        <button type="button" class="card dept" data-id="${d.id}"><c:out value="${d.name}"/></button>
      </c:forEach>
    </div>

    <div id="step2" hidden>
      <h2>2. Select Service</h2>
      <div class="grid">
        <c:forEach var="s" items="${services}">
          <button type="button" class="card svc" data-id="${s.id}" data-dept="${s.departmentId}" hidden>
            <c:out value="${s.name}"/>
          </button>
        </c:forEach>
      </div>
    </div>

    <div id="step3" hidden>
      <h2>3. Generate Token</h2>
      <button class="btn" type="submit">🎟️ Generate Token</button>
    </div>
  </form>
</div>
<script>
  var depts = document.querySelectorAll('.dept'), svcs = document.querySelectorAll('.svc');
  var step2 = document.getElementById('step2'), step3 = document.getElementById('step3');
  var deptInput = document.getElementById('departmentId'), svcInput = document.getElementById('serviceId');

  depts.forEach(function (b) {
    b.onclick = function () {
      depts.forEach(function (x) { x.classList.remove('sel'); });
      b.classList.add('sel');
      deptInput.value = b.dataset.id;
      svcInput.value = '';
      step3.hidden = true;
      step2.hidden = false;
      svcs.forEach(function (s) { s.classList.remove('sel'); s.hidden = (s.dataset.dept !== b.dataset.id); });
    };
  });
  svcs.forEach(function (b) {
    b.onclick = function () {
      svcs.forEach(function (x) { x.classList.remove('sel'); });
      b.classList.add('sel');
      svcInput.value = b.dataset.id;
      step3.hidden = false;
    };
  });
</script>
</body></html>
