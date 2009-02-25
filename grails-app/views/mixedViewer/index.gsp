<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <yui:stylesheet dir="calendar/assets" file="calendar.css" />
    <my:nickStyle persons="${nickPersonList}" />
    <g:javascript library="yui" />
    <yui:javascript dir="calendar" file="calendar.js" />
    <g:javascript library="common" />
    <g:javascript library="calendar" />
    <g:javascript library="mixedViewer" />
    <title><g:message code="mixedViewer" /></title>
  </head>
  <body>
    <h2 class="print"><g:message code="mixedViewer" /></h2>
    <div class="body viewer mixedViewer">
      <my:flashMessage />
      <g:render template="search" />
      <g:render template="list" />
    </div>
  </body>
</html>
