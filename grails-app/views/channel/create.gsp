<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
  <g:set var="entityName" value="${message(code: 'channel.label')}"/>
  <title><g:message code="default.create.label" args="[entityName]"/></title>
</head>

<body>
<div class="body channel">
  <irclog:flashMessage bean="${channel}"/>
  <irclog:withHelp id="channel-create-help">
    <h1><g:message code="default.create.label" args="[entityName]"/></h1>
  </irclog:withHelp>
  <irclog:help for="channel-create-help" visible="true">
    <g:message code="channel.create.caption"/>
  </irclog:help>
  <g:form action="save" method="post">
    <div class="dialog">
      <table>
        <tbody>
        <tr class="prop">
          <th valign="top" class="name">
            <label for="name"><g:message code="channel.name.label" default="Name"/>:</label>
          </th>
          <td valign="top" class="value ${hasErrors(bean: channel, field: 'name', 'errors')}">
            <input type="text" id="name" name="name" value="${fieldValue(bean: channel, field: 'name')}"/>
          </td>
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
            <g:checkBox name="isPrivate" value="${channel?.isPrivate}"></g:checkBox> <g:message code="channel.isPrivate.edit.caption"/>
          </td>
        </tr>
        <tr class="prop">
          <th valign="top" class="name">
            <label for="isArchived"><g:message code="channel.isArchived.label" default="Is Archived"/>:</label>
          </th>
          <td valign="top" class="value ${hasErrors(bean: channel, field: 'isArchived', 'errors')}">
            <g:checkBox name="isArchived" value="${channel?.isArchived}"></g:checkBox> <g:message code="channel.isArchived.edit.caption"/>
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
      <span class="button"><input class="save" type="submit" value="${message(code: 'default.button.create.label', 'default': 'Create')}"/></span>
    </div>
  </g:form>
</div>
</body>
</html>
