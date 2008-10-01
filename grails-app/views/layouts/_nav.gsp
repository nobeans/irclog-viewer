<%-- 未ログイン --%>
<g:isNotLoggedIn>
  <my:createNavLinkIfNotCurrent controller="viewer" />
  <my:createNavLinkIfNotCurrent controller="channel" action="list" />
  <my:createNavLinkIfNotCurrent controller="register" action="create" />
  <my:createNavLinkIfNotCurrent controller="login" />
  <span class="floatLeft" id="login-info">
    <img src="${createLinkTo(dir:'images',file:'guest.png')}"/><g:message code="login.info.guest" />
  </span>
</g:isNotLoggedIn>

<%-- ログイン中 --%>
<g:isLoggedIn>
  <my:createNavLinkIfNotCurrent controller="viewer" />
  <my:createNavLinkIfNotCurrent controller="channel" action="list" />
  <my:createNavLinkIfNotCurrent controller="register" action="show" />
  <my:createNavLinkIfNotCurrent controller="logout" />
  <span class="floatLeft" id="login-info">
    <img src="${createLinkTo(dir:'images',file:'person.png')}"/><g:message code="login.info" args="${[g.loggedInUserInfo(field:'loginName')]}" />
  </span>
</g:isLoggedIn>

&nbsp;
