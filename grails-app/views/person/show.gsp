<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <title><g:message code="person.show" default="Show Person" /></title>
  </head>
  <body>
    <div class="body">
      <my:flashMessage bean="${person}" />
      <h1><g:message code="person.show" default="Show Person" /></h1>
      <div class="dialog">
        <table>
          <tbody>
            <tr class="prop">
              <td valign="top" class="name"><g:message code="person.loginName" default="Login Name" />:</td>
              <td valign="top" class="value">${fieldValue(bean:person, field:'loginName')}</td>
            </tr>
            <tr class="prop">
              <td valign="top" class="name"><g:message code="person.realName" default="Real Name" />:</td>
              <td valign="top" class="value">${fieldValue(bean:person, field:'realName')}</td>
            </tr>
            <tr class="prop">
              <td valign="top" class="name"><g:message code="person.nicks" default="Nicks" />:</td>
              <td valign="top" class="value">${fieldValue(bean:person, field:'nicks')}</td>
            </tr>
            <tr class="prop">
              <td valign="top" class="name"><g:message code="person.color" default="Color" />:</td>
              <td valign="top" class="value">${fieldValue(bean:person, field:'color')}</td>
            </tr>
            <tr class="prop">
              <td valign="top" class="name"><g:message code="person.enabled" default="Enabled" />:</td>
              <td valign="top" class="value"><g:message code="person.enabled.${fieldValue(bean:person, field:'enabled')}" /></td>
            </tr>
            <tr class="prop">
              <td valign="top" class="name"><g:message code="person.roles" default="Roles" />:</td>
              <td valign="top" class="value">
                <g:each var="r" in="${person.roles}">
                  <g:message code="person.roles.${r}" />
                </g:each>
              </td>
            </tr>
            <tr class="prop">
              <td valign="top" class="name"><g:message code="person.channels" default="Channels" />:</td>
              <td  valign="top" style="text-align:left;" class="value">
                <ul>
                  <g:each var="c" in="${person.channels.sort{it.name}}">
                    <li><g:link controller="channel" action="show" id="${c.id}">${c.name.encodeAsHTML()}</g:link></li>
                  </g:each>
                </ul>
                <% if (person.channels.empty) { %>
                  <g:message code="person.channels.empty" />
                <% } %>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="buttons">
        <g:form>
          <input type="hidden" name="id" value="${person?.id}" />
          <span class="button"><g:actionSubmit class="edit" action="Edit" value="${message(code:'edit', 'default':'Edit')}" /></span>
          <span class="button"><g:actionSubmit class="delete" onclick="return confirm('${message(code:'delete.confirm', 'default':'Are you sure?')}');" action="Delete" value="${message(code:'delete', 'default':'Delete')}" /></span>
        </g:form>
      </div>
    </div>
  </body>
</html>
