<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <irclog:nickStyle persons="${nickPersonList}" classPrefix="" />
    <title><g:message code="summary" default="Summary" /></title>
  </head>
  <body>
    <div class="body summary">
      <irclog:flashMessage />
      <g:render template="topic" />
      <g:render template="statement" />
    </div>
  </body>
</html>
