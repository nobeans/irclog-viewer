<sec:authorize access="isAnonymous()">
  <ul>
    <irclog:createNavLinkIfNotCurrent controller="summary"/>
    <irclog:createNavLinkIfNotCurrent controller="search"/>
    <irclog:createNavLinkIfNotCurrent controller="channel" action="list"/>
    <irclog:createNavLinkIfNotCurrent controller="register" action="create"/>
    <irclog:createNavLinkIfNotCurrent controller="login"/>
  </ul>
  <ul id="login-info">
    <li><img src="${asset.assetPath(src: 'guest.png')}" alt="user-icon"/><irclog:loginUserInfo/></li>
  </ul>
</sec:authorize>

<sec:authorize access="!isAnonymous()">
  <ul>
    <irclog:createNavLinkIfNotCurrent controller="summary"/>
    <irclog:createNavLinkIfNotCurrent controller="search"/>
    <irclog:createNavLinkIfNotCurrent controller="channel" action="list"/>
    <sec:authorize access="hasRole('ADMIN')">
      <irclog:createNavLinkIfNotCurrent controller="person" action="list"/>
    </sec:authorize>
    <sec:authorize access="!hasRole('ADMIN')">
      <irclog:createNavLinkIfNotCurrent controller="register" action="show"/>
    </sec:authorize>
    <irclog:createNavLinkIfNotCurrent controller="logout"/>
  </ul>
  <ul id="login-info">
    <li><img src="${asset.assetPath(src: 'person.png')}" alt="user-icon"/><irclog:loginUserInfo/></li>
  </ul>
</sec:authorize>
