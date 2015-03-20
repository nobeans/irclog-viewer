<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
  <g:set var="entityName" value="${message(code: 'channel.label')}"/>
  <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>

<body>
<div class="body channel">
  <irclog:flashMessage bean="${channel}"/>
  <irclog:withHelp id="channel-edit-help">
    <h1><g:message code="default.edit.label" args="[entityName]"/></h1>
  </irclog:withHelp>
  <irclog:help for="channel-edit-help" visible="true">
    <g:message code="channel.edit.caption"/>
  </irclog:help>
  <g:form method="post">
    <input type="hidden" name="id" value="${channel?.id}"/>

    <div class="dialog">
      <table>
        <tbody>
        <tr class="prop">
          <th valign="top" class="name">
            <label for="name"><g:message code="channel.name.label" default="Name"/>:</label>
          </th>
          <td valign="top" class="value">${fieldValue(bean: channel, field: 'name')}</td>
        </tr>
        <tr class="prop">
          <th valign="top" class="name">
            <label for="description"><g:message code="channel.description.label" default="Description"/>:</label>
          </th>
          <td valign="top" class="value ${hasErrors(bean: channel, field: 'description', 'errors')}">
            <textarea id="description" name="description">${fieldValue(bean: channel, field: 'description')}</textarea>
          </td>
        </tr>
        <tr class="prop">
          <th valign="top" class="name">
            <label for="isPrivate"><g:message code="channel.isPrivate.label" default="Is Private"/>:</label>
          </th>
          <td valign="top" class="value ${hasErrors(bean: channel, field: 'isPrivate', 'errors')}">
            <g:checkBox name="isPrivate" value="${channel?.isPrivate}"></g:checkBox>
            <label for="isPrivate"><g:message code="channel.isPrivate.edit.caption"/></label>
          </td>
        </tr>
        <tr class="prop">
          <th valign="top" class="name">
            <label for="isArchived"><g:message code="channel.isArchived.label" default="Is Archived"/>:</label>
          </th>
          <td valign="top" class="value ${hasErrors(bean: channel, field: 'isArchived', 'errors')}">
            <g:checkBox name="isArchived" value="${channel?.isArchived}"></g:checkBox>
            <label for="isArchived"><g:message code="channel.isArchived.edit.caption"/></label>
          </td>
        </tr>
        <tr class="prop">
          <th valign="top" class="name">
            <label for="secretKey"><g:message code="channel.secretKey.label" default="Secret Key"/>:</label>
          </th>
          <td valign="top" class="value ${hasErrors(bean: channel, field: 'secretKey', 'errors')}">
            <input type="password" id="secretKey" name="secretKey" value="${fieldValue(bean: channel, field: 'secretKey')}"/>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <div class="buttons">
      <span class="button"><g:actionSubmit class="save" action="Update" value="${message(code: 'default.button.update.label', 'default': 'Update')}"/></span>
      <span class="button"><g:actionSubmit class="delete" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', 'default': 'Are you sure?')}');" action="Delete" value="${message(code: 'default.button.delete.label', 'default': 'Delete')}"/></span>
    </div>
  </g:form>
</div>
</body>
</html>
