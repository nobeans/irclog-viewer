<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
  <g:set var="entityName" value="${message(code: 'register.label')}"/>
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
        <td valign='top' class='name'>
          <g:message code="person.loginName.label"/>:
        </td>
        <td valign="top" class="value">
          ${person.loginName?.encodeAsHTML()}
        </td>
      </tr>
      <tr class="prop">
        <td valign='top' class='name'>
          <g:message code="person.realName.label"/>:
        </td>
        <td valign="top" class="value">
          ${person.realName?.encodeAsHTML()}
        </td>
      </tr>
      <tr class="prop">
        <td valign="top" class="name">
          <g:message code="person.nicks.label"/>:
        </td>
        <td valign="top" class="value">
          ${person.nicks?.encodeAsHTML()}
        </td>
      </tr>
      <tr class="prop">
        <td valign="top" class="name">
          <g:message code="person.color.label"/>:
        </td>
        <td valign="top" class="value" style="color:${person.color?.encodeAsHTML()}">
          ${person.color?.encodeAsHTML()}
        </td>
      </tr>
      </tbody>
    </table>
  </div>

  <div class="buttons">
    <span class="button"><g:link class="edit" action="edit" id="${person.id}"><g:message code="default.button.edit.label"/></g:link></span>
  </div>
</div>
</body>
</html>
