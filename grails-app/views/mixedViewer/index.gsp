<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <g:javascript library="yui" />
    <yui:javascript dir="calendar" file="calendar.js" />
    <yui:stylesheet dir="calendar/assets" file="calendar.css" />
    <g:javascript library="calendar" />
    <g:javascript library="mixedViewer" />
    <title><g:message code="mixedViewer" /></title>
  </head>
  <body>
    <div class="body viewer mixedViewer">
      <my:flashMessage />
      <g:render template="search" />
      <g:render template="list" />
    </div>
  </body>
</html>