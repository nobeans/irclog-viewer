<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <irclog:nickStyle persons="${nickPersonList}" classPrefix=".irclog-nick" />
    <r:require module="singleViewer" />
    <title><g:message code="singleViewer" args="${[criterion?.channel +'@'+ criterion['period-oneday-date']]}" /></title>
  </head>
  <body>
    <h2 class="print"><g:message code="singleViewer" args="${[criterion?.channel +'@'+ criterion['period-oneday-date']]}" /></h2>
    <div class="body viewer singleViewer">
      <irclog:flashMessage />
      <g:render template="list" />
    </div>
  </body>
</html>
