<%-- 未ログイン --%>
<g:isNotLoggedIn>
  <ul>
    <my:createNavLinkIfNotCurrent controller="login" />
    <my:createNavLinkIfNotCurrent controller="register" action="create" />
    <my:createNavLinkIfNotCurrent controller="channel" action="list" />
    <my:createNavLinkIfNotCurrent controller="mixedViewer" />
  </ul>
  <span class="floatLeft" id="login-info">
    <img src="${createLinkTo(dir:'images',file:'guest.png')}"/><g:message code="login.info.guest" />
  </span>
</g:isNotLoggedIn>

<%-- ログイン中 --%>
<g:isLoggedIn>
  <ul>
    <my:createNavLinkIfNotCurrent controller="logout" />
    <my:createNavLinkIfNotCurrent controller="register" action="show" />
    <my:createNavLinkIfNotCurrent controller="channel" action="list" />
    <my:createNavLinkIfNotCurrent controller="mixedViewer" />
  </ul>
  <span class="floatLeft" id="login-info">
    <img src="${createLinkTo(dir:'images',file:'person.png')}"/><g:message code="login.info" args="${[g.loggedInUserInfo(field:'loginName')]}" />
  </span>
</g:isLoggedIn>

&nbsp;
