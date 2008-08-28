<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <title><g:message code="viewer.index" /></title>
  </head>
  <body>
    <div class="body">
      <g:if test="${flash.message}">
      <div class="message"><g:message code="${flash.message}" args="${flash.args}" default="${flash.defaultMessage}" /></div>
      </g:if>
      <g:render template="search" />
      <g:render template="list" />
    </div>
  </body>
</html>
