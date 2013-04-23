<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <g:external type="css" uri="${createLink(controller: "dynamicCss", action: "nickColors")}"/>
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
