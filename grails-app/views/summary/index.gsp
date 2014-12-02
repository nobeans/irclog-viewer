<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <g:external type="css" uri="${createLink(controller: "dynamicCss", action: "nickColors")}"/>
  <title><g:message code="summary" default="Summary"/></title>
</head>

<body>
<div class="body summary">
  <irclog:flashMessage/>
  <g:render template="topic"/>
  <g:render template="statement"/>
</div>
</body>
</html>
