<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
  <g:set var="entityName" value="${message(code: 'person.label')}"/>
  <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<div class="body">
  <irclog:flashMessage bean="${person}"/>
  <h1><g:message code="default.show.label" args="[entityName]"/></h1>

  <div class="dialog">
    <table>
      <tbody>
      <tr class="prop">
        <td valign="top" class="name"><g:message code="person.loginName.label" default="Login Name"/>:</td>
        <td valign="top" class="value">${fieldValue(bean: person, field: 'loginName')}</td>
      </tr>
      <tr class="prop">
        <td valign="top" class="name"><g:message code="person.realName.label" default="Real Name"/>:</td>
        <td valign="top" class="value">${fieldValue(bean: person, field: 'realName')}</td>
      </tr>
      <tr class="prop">
        <td valign="top" class="name"><g:message code="person.nicks.label" default="Nicks"/>:</td>
        <td valign="top" class="value">${fieldValue(bean: person, field: 'nicks')}</td>
      </tr>
      <tr class="prop">
        <td valign="top" class="name"><g:message code="person.color.label" default="Color"/>:</td>
        <td valign="top" class="value" style="color:${fieldValue(bean: person, field: 'color')}">${fieldValue(bean: person, field: 'color')}</td>
      </tr>
      <tr class="prop">
        <td valign="top" class="name"><g:message code="person.enabled.label" default="Enabled"/>:</td>
        <td valign="top" class="value"><g:message code="person.enabled.label_of.${fieldValue(bean: person, field: 'enabled')}"/></td>
      </tr>
      <tr class="prop">
        <td valign="top" class="name"><g:message code="person.roles.label" default="Roles"/>:</td>
        <td valign="top" class="value">
          <g:each var="r" in="${person.roles}">
            <g:message code="person.roles.label_of.${r}"/>
          </g:each>
          <% if (person.isAdmin()) { %>
          &gt;&gt; <g:link id="${person?.id}" action="toUser"><g:message code="person.toUser.button.label"/></g:link></li>
          <% } else { %>
          &gt;&gt; <g:link id="${person?.id}" action="toAdmin"><g:message code="person.toAdmin.button.label"/></g:link></li>
          <% } %>
        </td>
      </tr>
      <tr class="prop">
        <td valign="top" class="name"><g:message code="person.channels.label" default="Channels"/>:</td>
        <td valign="top" style="text-align:left;" class="value">
          <ul>
            <g:each var="c" in="${person.channels.sort { it.name }}">
              <li><g:link controller="channel" action="show" id="${c.id}">${c.name.encodeAsHTML()}</g:link></li>
            </g:each>
          </ul>
          <% if (person.channels.empty) { %>
          <g:message code="empty.label"/>
          <% } %>
        </td>
      </tr>
      </tbody>
    </table>
  </div>

  <div class="buttons">
    <g:form>
      <input type="hidden" name="id" value="${person?.id}"/>
      <span class="button"><g:actionSubmit class="edit" action="Edit" value="${message(code: 'default.button.edit.label', 'default': 'Edit')}"/></span>
      <span class="button"><g:actionSubmit class="delete" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', 'default': 'Are you sure?')}');" action="Delete" value="${message(code: 'default.button.delete.label', 'default': 'Delete')}"/></span>
    </g:form>
  </div>
</div>
</body>
</html>
