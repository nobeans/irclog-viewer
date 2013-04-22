<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <irclog:nickStyle persons="${nickPersonList}" classPrefix=".irclog-nick"/>
  <r:require module="singleViewer"/>
  <title><g:message code="singleViewer" args="${[criterion?.channel + '@' + criterion['periodOnedayDate']]}"/></title>
</head>

<body>
<h2 class="print"><g:message code="singleViewer" args="${[criterion?.channel + '@' + criterion['periodOnedayDate']]}"/></h2>

<div class="body viewer singleViewer">
  <irclog:flashMessage/>
  <g:render template="condition"/>
  <g:render template="list"/>
</div>
</body>
</html>
