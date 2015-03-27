<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
  <g:set var="entityName" value="${message(code: 'channel.label')}"/>
  <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<div class="body channel">
  <irclog:flashMessage/>
  <h1><g:message code="default.show.label" args="[entityName]"/></h1>

  <div class="dialog">
    <table>
      <tbody>
      <tr class="prop">
        <th valign="top" class="name"><g:message code="channel.name.label" default="Name"/>:</th>
        <td valign="top" class="value">${fieldValue(bean: channel, field: 'name')}</td>
      </tr>
      <tr class="prop">
        <th valign="top" class="name"><g:message code="channel.description.label" default="Description"/>:</th>
        <td valign="top" class="value"><pre>${fieldValue(bean: channel, field: 'description')}</pre></td>
      </tr>
      <tr class="prop">
        <th valign="top" class="name"><g:message code="channel.isPrivate.label" default="Is Private"/>:</th>
        <td valign="top" class="value"><g:message code="channel.isPrivate.label_of.${channel.isPrivate.toString()}"/></td>
      </tr>
      <tr class="prop">
        <th valign="top" class="name"><g:message code="channel.isArchived.label" default="Is Archived"/>:</th>
        <td valign="top" class="value"><g:message code="channel.isArchived.label_of.${channel.isArchived.toString()}"/></td>
      </tr>
      <tr class="prop">
        <th valign="top" class="name"><g:message code="channel.secretKey.label" default="Secret Key"/>:</th>
        <td valign="top" class="value">${channel.secretKey ? '****' : ''}</td>
      </tr>
      <tr class="prop">
        <td valign="top" class="name"><g:message code="channel.joinedPersons.label" default="Joined Persons"/>:</td>
        <td valign="top" style="text-align:left;" class="value">
          <ul>
            <g:each var="p" in="${channel.persons.sort { it.loginName }}">
              <li title="${p.realName.encodeAsHTML()}">
                <sec:authorize access="hasRole('ADMIN')">
                  <g:link controller="person" action="show" id="${p.id}">${p.loginName.encodeAsHTML()}</g:link>
                  &gt;&gt; <g:link controller="channel" action="kick" id="${channel.id}" params="[personId: p.id]" onclick="return confirm('${message(code: 'channel.kick.confirm.message')}');">
                  <g:message code="channel.kick.label"/>
                </g:link>
                </sec:authorize>
                <sec:authorize access="!hasRole('ADMIN')">
                  ${p.loginName.encodeAsHTML()}
                </sec:authorize>
              </li>
            </g:each>
          </ul>
          <% if (channel.persons.empty) { %>
          <g:message code="empty.label"/>
          <% } %>
        </td>
      </tr>
      </tbody>
    </table>
  </div>

  <div class="buttons">
    <g:form>
      <input type="hidden" name="id" value="${channel?.id}"/>
      <span class="button"><input type="button" class="search" onclick="document.location = '${irclog.searchAllLogsLink(channel:channel)}'" value="${message(code: 'channel.searchAllLogs.button.label')}"/></span>
      <sec:authorize access="isAuthenticated()">
        <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label')}"/></span>
        <span class="button"><g:actionSubmit class="delete" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', 'default': 'Are you sure?')}');" action="Delete" value="${message(code: 'default.button.delete.label', 'default': 'Delete')}"/></span>
        <% if (channel.persons.find { it.loginName == loginUserName } != null) { %>
        <span class="button"><g:actionSubmit class="part" action="part" value="${message(code: 'channel.part.label')}" onclick="return confirm('${message(code: 'channel.part.confirm.message')}');"/></span>
        <% } %>
      </sec:authorize>
    </g:form>
  </div>
</div>
</body>
</html>
