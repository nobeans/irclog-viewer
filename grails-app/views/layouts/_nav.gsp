<sec:ifNotLoggedIn>
  <ul>
    <irclog:createNavLinkIfNotCurrent controller="summary" />
    <irclog:createNavLinkIfNotCurrent controller="mixedViewer" />
    <irclog:createNavLinkIfNotCurrent controller="channel" action="list" />
    <irclog:createNavLinkIfNotCurrent controller="register" action="create" />
    <irclog:createNavLinkIfNotCurrent controller="login" />
  </ul>
  <ul id="login-info">
    <li><img src="${resource(dir:'images',file:'guest.png')}" alt="Guest user" /><g:message code="login.info.guest" /></li>
  </ul>
</sec:ifNotLoggedIn>

<sec:ifLoggedIn>
  <ul>
    <irclog:createNavLinkIfNotCurrent controller="summary" />
    <irclog:createNavLinkIfNotCurrent controller="mixedViewer" />
    <irclog:createNavLinkIfNotCurrent controller="channel" action="list" />
    <sec:ifAnyGranted roles="ROLE_ADMIN">
      <irclog:createNavLinkIfNotCurrent controller="person" action="list" />
    </sec:ifAnyGranted>
    <sec:ifNotGranted roles="ROLE_ADMIN">
      <irclog:createNavLinkIfNotCurrent controller="register" action="show" />
    </sec:ifNotGranted>
    <irclog:createNavLinkIfNotCurrent controller="logout" />
  </ul>
  <ul id="login-info">
    <li><img src="${resource(dir:'images',file:'person.png')}" alt="Logged-in user" /><g:message code="login.info" args="${[my.loggedInPersonInfo(field:'realName'), my.loggedInPersonInfo(field:'loginName')]}"/></li>
  </ul>
</sec:ifLoggedIn>
