<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <r:require module="detail"/>
  <g:external type="css" uri="${createLink(controller: "dynamicCss", action: "nickColors")}"/>
  <title><g:message code="detail" args="${[criterion?.channel + '@' + criterion['periodOnedayDate']]}"/></title>
</head>

<body>
<h2 class="print"><g:message code="detail" args="${[criterion?.channel + '@' + criterion['periodOnedayDate']]}"/></h2>

<div class="body viewer detail">
  <irclog:flashMessage/>
  <g:render template="condition"/>
  <g:render template="list"/>
</div>
</body>
</html>
