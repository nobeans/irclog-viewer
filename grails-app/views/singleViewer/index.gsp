<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <yui:stylesheet dir="calendar/assets" file="calendar.css" />
    <my:nickStyle persons="${nickPersonList}" classPrefix=".irclog-nick" />
    <yui:javascript dir="calendar" file="calendar.js" />
    <g:javascript src="calendar.js" />
    <g:javascript src="singleViewer.js" />
    <title><g:message code="singleViewer" args="${[criterion?.channel +'@'+ criterion['period-oneday-date']]}" /></title>
  </head>
  <body>
    <h2 class="print"><g:message code="singleViewer" args="${[criterion?.channel +'@'+ criterion['period-oneday-date']]}" /></h2>
    <div class="body viewer singleViewer">
      <my:flashMessage />
      <g:render template="list" />
    </div>
  </body>
</html>
