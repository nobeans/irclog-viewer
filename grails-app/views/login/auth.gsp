<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title><g:message code="springSecurity.login.title"/></title>
</head>

<body>
<div id="login">
  <div class="inner">
    <div class="fheader"><g:message code="springSecurity.login.header"/></div>

    <g:if test="${errorMessage}">
      <div class="login_message">${errorMessage}</div>
    </g:if>

    <form action="${createLink(uri: '/login')}" method="POST" id="loginForm" class="cssform" autocomplete="off">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      <p>
        <label for="username"><g:message code="springSecurity.login.username.label"/>:</label>
        <input type="text" class="text_" name="username" id="username" autofocus="autofocus"/>
      </p>

      <p>
        <label for="password"><g:message code="springSecurity.login.password.label"/>:</label>
        <input type="password" class="text_" name="password" id="password"/>
      </p>

      <p id="remember_me_holder">
        <input type="checkbox" class="chk" name="_spring_security_remember_me" id="remember_me" <% if (hasCookie) { %>checked="checked"<% } %>/>
        <label for="remember_me"><g:message code="springSecurity.login.remember.me.label"/></label>
      </p>

      <p>
        <input type="submit" id="submit" value="${message(code: "springSecurity.login.button")}"/>
      </p>
    </form>
  </div>
</div>
</body>
</html>
