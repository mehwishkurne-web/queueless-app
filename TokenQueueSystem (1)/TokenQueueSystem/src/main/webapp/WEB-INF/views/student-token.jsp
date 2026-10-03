<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Your Token</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body>
<div class="topbar">
  <span>👋 <c:out value="${sessionScope.studentName}"/></span>
  <a href="${pageContext.request.contextPath}/student-auth?action=logout">Logout</a>
</div>
<div class="container narrow">
  <div class="ticket center">
    <div>YOUR TOKEN NUMBER</div>
    <div class="big" id="label">${token.tokenLabel}</div>
    <p><b><c:out value="${token.department}"/></b> &mdash; <c:out value="${token.service}"/></p>
  </div>

  <div class="row">
    <div>Now Serving<b id="serving">---</b></div>
    <div>People Ahead<b id="ahead">-</b></div>
  </div>

  <div class="panel center" id="waitBox">
    ⏳ Please wait. This page checks the queue automatically.
    <br><button class="btn grey" type="button" onclick="check()">Refresh now</button>
  </div>
  <div class="called" id="calledBox" hidden>🔔 Your token has been called!<br>Go to the Counter.</div>
  <div class="panel center" id="doneBox" hidden>
    ✅ Service completed.<br>
    <a class="btn" href="${pageContext.request.contextPath}/student/dashboard">Back to Dashboard</a>
  </div>
</div>

<script>
  var ctx = '${pageContext.request.contextPath}';
  var timer = null;
  function $(id) { return document.getElementById(id); }

  function check() {
    fetch(ctx + '/student/status', { cache: 'no-store' })
      .then(function (r) { return r.json(); })
      .then(function (d) {
        if (d.status === 'NONE') { location.href = ctx + '/student/dashboard'; return; }
        $('serving').textContent = d.nowServing;
        $('ahead').textContent = d.ahead;
        $('waitBox').hidden = d.status !== 'WAITING';
        $('calledBox').hidden = d.status !== 'CALLED';
        $('doneBox').hidden = d.status !== 'COMPLETED';
        if (d.status === 'COMPLETED') { clearInterval(timer); }
      })
      .catch(function () { /* network hiccup - try again next tick */ });
  }
  check();
  timer = setInterval(check, 4000);
</script>
</body></html>
