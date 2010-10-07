<%-- 未ログイン --%>
<sec:ifNotLoggedIn>
  <ul>
    <my:createNavLinkIfNotCurrent controller="summary" />
    <my:createNavLinkIfNotCurrent controller="mixedViewer" />
    <my:createNavLinkIfNotCurrent controller="channel" action="list" />
    <my:createNavLinkIfNotCurrent controller="register" action="create" />
    <my:createNavLinkIfNotCurrent controller="login" />
  </ul>
  <ul id="login-info">
    <li><img src="${resource(dir:'images',file:'guest.png')}" alt="Guest user" /><g:message code="login.info.guest" /></li>
  </ul>
</sec:ifNotLoggedIn>

<%-- ログイン中 --%>
<sec:ifLoggedIn>
  <ul>
    <my:createNavLinkIfNotCurrent controller="summary" />
    <my:createNavLinkIfNotCurrent controller="mixedViewer" />
    <my:createNavLinkIfNotCurrent controller="channel" action="list" />
    <sec:ifAnyGranted role="ROLE_ADMIN">
      <my:createNavLinkIfNotCurrent controller="person" action="list" />
    </sec:ifAnyGranted>
    <sec:ifNotGranted role="ROLE_ADMIN">
      <my:createNavLinkIfNotCurrent controller="register" action="show" />
    </sec:ifNotGranted>
    <my:createNavLinkIfNotCurrent controller="logout" />
  </ul>
  <ul id="login-info">
    <li><img src="${resource(dir:'images',file:'person.png')}" alt="Logged-in user" /><g:message code="login.info" args="${[g.loggedInUserInfo(field:'realName'), g.loggedInUserInfo(field:'loginName')]}" /></li>
  </ul>
</sec:ifLoggedIn>
