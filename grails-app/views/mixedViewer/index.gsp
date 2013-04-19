<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <irclog:nickStyle persons="${nickPersonList}" classPrefix=".irclog-nick"/>
  <r:require module="mixedViewer"/>
  <title><g:message code="mixedViewer"/></title>
</head>

<body>
<h2 class="print"><g:message code="mixedViewer"/></h2>

<div class="body viewer mixedViewer">
  <irclog:flashMessage/>
  <g:render template="search"/>
  <g:render template="list"/>
</div>
</body>
</html>
