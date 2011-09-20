<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <yui:stylesheet dir="calendar/assets" file="calendar.css" />
    <my:nickStyle persons="${nickPersonList}" classPrefix=".irclog-nick" />
    <g:javascript src="yui/yui.js" />
    <yui:javascript dir="calendar" file="calendar.js" />
    <g:javascript src="calendar.js" />
    <g:javascript src="dateformat.js" />
    <g:javascript src="mixedViewer.js" />
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
