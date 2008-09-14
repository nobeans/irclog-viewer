<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <g:javascript library="yui" />
    <yui:javascript dir="calendar" file="calendar.js" />
    <yui:stylesheet dir="calendar/assets" file="calendar.css" />
    <g:javascript library="calendar" />
    <g:javascript library="viewer" />
  </head>
  <body>
    <div class="body viewer">
      <g:if test="${flash.message}">
        <div class="message"><g:message code="${flash.message}" args="${flash.args}" default="${flash.defaultMessage}" /></div>
      </g:if>
      <g:render template="search" />
      <g:render template="list" />
    </div>
  </body>
</html>
