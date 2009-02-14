<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <title><g:message code="person.list" default="Person List" /></title>
  </head>
  <body>
    <div class="body">
      <my:flashMessage />
      <h1><g:message code="person.list" default="Person List" /></h1>
      <div class="list">
        <table>
          <thead>
            <tr>
              <g:sortableColumn property="loginName" title="Login Name" titleKey="person.loginName" />
              <g:sortableColumn property="realName" title="Real Name" titleKey="person.realName" />
              <g:sortableColumn property="nicks" title="Nicks" titleKey="person.nicks" />
              <g:sortableColumn property="color" title="Color" titleKey="person.color" />
            </tr>
          </thead>
          <tbody>
            <g:each in="${personList}" status="i" var="person">
              <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td><g:link action="show" id="${person.id}">${fieldValue(bean:person, field:'loginName')}</g:link></td>
                <td>${fieldValue(bean:person, field:'realName')}</td>
                <td>${fieldValue(bean:person, field:'nicks')}</td>
                <td style="color:${fieldValue(bean:person, field:'color')}">${fieldValue(bean:person, field:'color')}</td>
              </tr>
            </g:each>
          </tbody>
        </table>
      </div>
      <div class="paginateButtons">
        <% def beginIndex = Math.min(params.offset + 1, personCount) %>
        <% def endIndex = Math.min(params.offset + params.max, personCount) %>
        <% def totalCount = personCount %>
        <g:paginate total="${totalCount}" />
        <span class="count"><g:message code="paginate.count" args="${[beginIndex, endIndex, totalCount]}"/></span>
      </div>
      <div class="buttons">
        <span class="menuButton"><g:link class="create" action="create"><g:message code="person.create" default="New Person" /></g:link></span>
      </div>
    </div>
  </body>
</html>
