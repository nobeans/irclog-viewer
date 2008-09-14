<div class="toolbar">
  <%-- 未ログイン --%>
  <g:isNotLoggedIn>
    <div class="buttons">
      <input type="button" class="login" value="ログイン..." onclick="$$('.subbar').each(function(e){e.hide()}); $('login').toggle()"/>
      <input type="button" class="login" value="ユーザ登録..." onclick="$$('.subbar').each(function(e){e.hide()}); $('register').toggle()"/>
    </div>
    <div class="subbar" id="login" style="display:none">
      <g:form controller="j_spring_security_check" method="post">
        <label for="loginName"><g:message code="person.loginName" />:</label>
          <input id="loginName" type="text" name="j_username" value=""></input>
        <label for="password"><g:message code="person.password" />:</label>
          <input id="password" type="password" name="j_password" value=""></input>
        <input type='checkbox' class='chk' id='remember_me' name='_spring_security_remember_me'>
          <label for='remember_me'><g:message code="toolbar.login.rememberMe" /></label>
        <input class="image" type="image" src="${createLinkTo(dir:'images', file:'go.png')}"
            title="${message(code:'toolbar.login.tooltips')}" />
      </g:form>
    </div>
    <div class="subbar" id="register" style="display:none">
      <g:form controller="register" action="save" method="post">
        <label for="loginName"><g:message code="person.loginName" />:</label>
          <input id="loginName" type="text" name="loginName" value=""></input>
        <label for="password"><g:message code="person.password" />:</label>
          <input id="password" type="password" name="password" value=""></input>
        <label for="password"><g:message code="person.repassword" />:</label>
          <input id="repassword" type="password" name="repassword" value=""></input>
        <input class="image" type="image" src="${createLinkTo(dir:'images', file:'go.png')}"
            title="${message(code:'toolbar.register.tooltips')}" />
      </g:form>
    </div>
  </g:isNotLoggedIn>

  <%-- ログイン中 --%>
  <g:isLoggedIn>
    <div class="buttons">
      <g:form controller="logout" method="get"><input type="submit" class="logout" value="ログアウト"/></g:form>
      <span class="floatLeft" id="login-info">
        <span><g:message code="toolbar.login.info" args="${[g.loggedInUserInfo(field:'loginName')]}" /></span>
      </span>
    </div>
  </g:isLoggedIn>
</div>
