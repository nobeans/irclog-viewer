<div class="header">
  <h1><g:message code="application.name" /></h1>
  <div id="auth">
    <g:isLoggedIn>
      <div id="auth-info">
        <span><g:message code="auth.info" args="${[g.loggedInUserInfo(field:'loginName')]}" /></span>
        <g:link class="logout" controller="logout">
          <img class="button" src="${createLinkTo(dir:'images', file:'logout.png')}" title="${message(code:'auth.logout.tooltips')}" />
        </g:link>
      </div>
    </g:isLoggedIn>
    <g:isNotLoggedIn>
      <g:form controller="j_spring_security_check" method="get">
        <label for="auth-loginName"><g:message code="person.loginName" />:</label>
          <input id="auth-loginName" type="text" name="j_username" value=""></input>
        <label for="auth-password"><g:message code="person.password" />:</label>
          <input id="auth-password" type="password" name="j_password" value=""></input>
        <input type='checkbox' class='chk' id='remember_me' name='_spring_security_remember_me'>
          <label for='remember_me'><g:message code="auth.login.rememberMe" /></label>
        <input class="image" type="image" src="${createLinkTo(dir:'images', file:'login.png')}"
            title="${message(code:'auth.login.tooltips')}" />
      </g:form>
    </g:isNotLoggedIn>
  </div>
</div>
