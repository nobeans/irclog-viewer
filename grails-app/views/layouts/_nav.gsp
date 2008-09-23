<%-- 未ログイン --%>
<g:isNotLoggedIn>
  <my:createNavLinkIfNotCurrent name="viewer" />
  <my:createNavLinkIfNotCurrent name="channel" action="list" />
  <my:createNavLinkIfNotCurrent name="register" />
  <my:createNavLinkIfNotCurrent name="login" />
  <span class="floatLeft" id="login-info">
    <img src="${createLinkTo(dir:'images',file:'guest.png')}"/><g:message code="login.info.guest" />
  </span>
</g:isNotLoggedIn>

<%-- ログイン中 --%>
<g:isLoggedIn>
  <my:createNavLinkIfNotCurrent name="viewer" />
  <my:createNavLinkIfNotCurrent name="channel" action="list" />
  <my:createNavLinkIfNotCurrent name="logout" />
  <span class="floatLeft" id="login-info">
    <img src="${createLinkTo(dir:'images',file:'person.png')}"/><g:message code="login.info" args="${[g.loggedInUserInfo(field:'loginName')]}" />
  </span>
</g:isLoggedIn>

&nbsp;
