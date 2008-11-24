<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <title><g:message code="channel.show" default="Show Channel" /></title>
  </head>
  <body>
    <div class="body">
      <my:flashMessage />
      <h1><g:message code="channel.show" default="Show Channel" /></h1>
      <div class="dialog">
        <table>
          <tbody>
            <tr class="prop">
              <th valign="top" class="name"><g:message code="channel.name" default="Name" />:</th>
              <td valign="top" class="value">${fieldValue(bean:channel, field:'name')}</td>
            </tr>
            <tr class="prop">
              <th valign="top" class="name"><g:message code="channel.description" default="Description" />:</th>
              <td valign="top" class="value"><pre>${fieldValue(bean:channel, field:'description')}</pre></td>
            </tr>
            <tr class="prop">
              <th valign="top" class="name"><g:message code="channel.isPrivate" default="Is Private" />:</th>
              <td valign="top" class="value"><g:message code="channel.isPrivate.${channel.isPrivate.toString()}" /></td>
            </tr>
            <tr class="prop">
              <th valign="top" class="name"><g:message code="channel.secretKey" default="Secret Key" />:</th>
              <td valign="top" class="value">${channel.secretKey ? '****' : ''}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <g:isLoggedIn>
        <div class="buttons">
          <g:form>
            <input type="hidden" name="id" value="${channel?.id}" />
            <span class="button"><g:actionSubmit class="edit" action="Edit" value="${message(code:'edit', 'default':'Edit')}" /></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('${message(code:'delete.confirm', 'default':'Are you sure?')}');" action="Delete" value="${message(code:'delete', 'default':'Delete')}" /></span>
          </g:form>
        </div>
      </g:isLoggedIn>
    </div>
  </body>
</html>
