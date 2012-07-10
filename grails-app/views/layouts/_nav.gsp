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

<sec:ifLoggedIn>
  <ul>
    <my:createNavLinkIfNotCurrent controller="summary" />
    <my:createNavLinkIfNotCurrent controller="mixedViewer" />
    <my:createNavLinkIfNotCurrent controller="channel" action="list" />
    <sec:ifAnyGranted roles="ROLE_ADMIN">
      <my:createNavLinkIfNotCurrent controller="person" action="list" />
    </sec:ifAnyGranted>
    <sec:ifNotGranted roles="ROLE_ADMIN">
      <my:createNavLinkIfNotCurrent controller="register" action="show" />
    </sec:ifNotGranted>
    <my:createNavLinkIfNotCurrent controller="logout" />
  </ul>
  <ul id="login-info">
    <li><img src="${resource(dir:'images',file:'person.png')}" alt="Logged-in user" /><g:message code="login.info" args="${[my.loggedInPersonInfo(field:'realName'), my.loggedInPersonInfo(field:'loginName')]}"/></li>
  </ul>
</sec:ifLoggedIn>
