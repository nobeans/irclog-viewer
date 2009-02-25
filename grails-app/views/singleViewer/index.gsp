<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <g:javascript library="common" />
    <g:javascript library="singleViewer" />
    <title><g:message code="singleViewer" args="${[criterion?.channel +'@'+ criterion['period-oneday-date']]}" /></title>
    <my:nickStyle persons="${nickPersonList}" />
  </head>
  <body>
    <h2 class="print"><g:message code="singleViewer" args="${[criterion?.channel +'@'+ criterion['period-oneday-date']]}" /></h2>
    <div class="body viewer singleViewer">
      <my:flashMessage />
      <g:render template="list" />
    </div>
  </body>
</html>
