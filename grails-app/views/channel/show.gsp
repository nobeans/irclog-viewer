<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <g:javascript library="channel" />
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
              <th valign="top" class="name"><g:message code="channel.isArchived" default="Is Archived" />:</th>
              <td valign="top" class="value"><g:message code="channel.isArchived.${channel.isArchived.toString()}" /></td>
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
                    <li title="${p.realName.encodeAsHTML()}">
                      <sec:ifAnyGranted role="ROLE_ADMIN">
                        <g:link controller="person" action="show" id="${p.id}">${p.loginName.encodeAsHTML()}</g:link>
                        &gt;&gt; <g:link controller="channel" action="kick" id="${channel.id}" params="[personId:p.id]" onclick="return confirm('${message(code:'channel.kick.confirm')}');">
                            <g:message code="channel.kick" />
                        </g:link>
                      </sec:ifAnyGranted>
                      <sec:ifNotGranted role="ROLE_ADMIN">
                        ${p.loginName.encodeAsHTML()}
                      </sec:ifNotGranted>
                    </li>
                  </g:each>
                </ul>
                <% if (joinedPersons.empty) { %>
                  <g:message code="empty" />
                <% } %>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="buttons">
        <g:form>
          <input type="hidden" name="id" value="${channel?.id}" />
          <span class="button"><input type="button" class="search" onclick="IRCLOG.goto('${my.searchAllLogsLink(channel:channel)}')" value="${message(code:'channel.searchAllLogs')}" /></span>
          <sec:ifLoggedIn>
            <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code:'edit')}" /></span>
            <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code:'delete')}" onclick="return confirm('${message(code:'delete.confirm')}');" /></span>
            <% if (joinedPersons.find{it.loginName == loginUserName} != null) { %>
              <span class="button"><g:actionSubmit class="part" action="part" value="${message(code:'channel.part')}" onclick="return confirm('${message(code:'channel.part.confirm')}');" /></span>
            <% } %>
          </sec:ifLoggedIn>
        </g:form>
      </div>
    </div>
  </body>
</html>
