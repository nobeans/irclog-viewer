<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <title><g:message code="channel.show" default="Show Channel" /></title>
  </head>
  <body>
    <div class="body channel">
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
            <tr class="prop">
              <td valign="top" class="name"><g:message code="channel.joinedPersons" default="Joined Persons" />:</td>
              <td  valign="top" style="text-align:left;" class="value">
                <ul>
                  <g:each var="p" in="${joinedPersons.sort{it.loginName}}">
                    <li title="${p.realName.encodeAsHTML()}"><g:link controller="person" action="show" id="${p.id}">${p.loginName.encodeAsHTML()}</g:link></li>
                  </g:each>
                </ul>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="buttons">
        <g:form>
          <span class="button clickable"><img src="${createLinkTo(dir:'images', file:'search.png')}" alt="Search all logs" onclick="document.location='${my.searchAllLogsLink(channel:channel)}'" /><input type="button" onclick="document.location='${my.searchAllLogsLink(channel:channel)}'" value="${message(code:'channel.searchAllLogs')}" /></span>
          <input type="hidden" name="id" value="${channel?.id}" />
          <g:isLoggedIn>
            <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code:'edit', 'default':'Edit')}" /></span>
            <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code:'delete', 'default':'Delete')}" onclick="return confirm('${message(code:'delete.confirm', 'default':'Are you sure?')}');" /></span>
          </g:isLoggedIn>
        </g:form>
      </div>
    </div>
  </body>
</html>
