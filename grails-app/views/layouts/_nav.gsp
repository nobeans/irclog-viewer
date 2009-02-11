<%-- 未ログイン --%>
<g:isNotLoggedIn>
  <ul>
    <my:createNavLinkIfNotCurrent controller="login" />
    <my:createNavLinkIfNotCurrent controller="register" action="create" />
    <my:createNavLinkIfNotCurrent controller="channel" action="list" />
    <my:createNavLinkIfNotCurrent controller="mixedViewer" />
  </ul>
  <ul id="login-info">
    <img src="${createLinkTo(dir:'images',file:'guest.png')}" alt="Guest user" /><g:message code="login.info.guest" />
  </ul>
</g:isNotLoggedIn>

<%-- ログイン中 --%>
<g:isLoggedIn>
  <ul>
    <my:createNavLinkIfNotCurrent controller="logout" />
    <my:createNavLinkIfNotCurrent controller="register" action="show" />
    <my:createNavLinkIfNotCurrent controller="channel" action="list" />
    <my:createNavLinkIfNotCurrent controller="mixedViewer" />
  </ul>
  <ul id="login-info">
    <li><img src="${createLinkTo(dir:'images',file:'person.png')}" alt="Logged-in user" /></li>
    <li><g:message code="login.info" args="${[g.loggedInUserInfo(field:'realName'), g.loggedInUserInfo(field:'loginName')]}" /></li>
  </ul>
</g:isLoggedIn>
