<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <g:external type="css" uri="${createLink(controller: "dynamicCss", action: "nickColors")}"/>
  <title><g:message code="search"/></title>
</head>

<body>
<h2 class="print"><g:message code="search"/></h2>

<div class="body viewer search">
  <irclog:flashMessage/>
  <g:render template="criteria"/>
  <g:render template="list"/>
</div>
</body>
</html>
