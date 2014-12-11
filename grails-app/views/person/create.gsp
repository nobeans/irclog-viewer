<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
  <g:set var="entityName" value="${message(code: 'person.label')}"/>
  <title><g:message code="default.create.label" args="[entityName]"/></title>
</head>

<body>
<div class="body">
  <irclog:flashMessage bean="${person}"/>
  <h1><g:message code="default.create.label" args="[entityName]"/></h1>
  <g:form action="save" method="post">
    <div class="dialog">
      <table>
        <tbody>
        <tr class="prop">
          <td valign="top" class="name">
            <label for="loginName"><g:message code="person.loginName.label" default="Login Name"/>:</label>
          </td>
          <td valign="top" class="value ${hasErrors(bean: person, field: 'loginName', 'errors')}">
            <input type="text" id="loginName" name="loginName" value="${fieldValue(bean: person, field: 'loginName')}"/>
          </td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">
            <label for="realName"><g:message code="person.realName.label" default="Real Name"/>:</label>
          </td>
          <td valign="top" class="value ${hasErrors(bean: person, field: 'realName', 'errors')}">
            <input type="text" maxlength="100" id="realName" name="realName" value="${fieldValue(bean: person, field: 'realName')}"/>
          </td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">
            <label for="password"><g:message code="person.password.label" default="Password"/>:</label>
          </td>
          <td valign="top" class="value ${hasErrors(bean: person, field: 'password', 'errors')}">
            <input type="password" id="password" name="password" value="${fieldValue(bean: person, field: 'password')}"/>
          </td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">
            <label for="repassword"><g:message code="person.repassword.label" default="Repassword"/>:</label>
          </td>
          <td valign="top" class="value ${hasErrors(bean: person, field: 'password', 'errors')}">
            <input type="password" name="repassword" id="repassword" value="${fieldValue(bean: person, field: 'repassword')}"/>
          </td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">
            <label for="nicks"><g:message code="person.nicks.label" default="Nicks"/>:</label>
          </td>
          <td valign="top" class="value ${hasErrors(bean: person, field: 'nicks', 'errors')}">
            <input type="text" id="nicks" name="nicks" value="${fieldValue(bean: person, field: 'nicks')}"/>
          </td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">
            <label for="color"><g:message code="person.color.label" default="Color"/>:</label>
          </td>
          <td valign="top" class="value ${hasErrors(bean: person, field: 'color', 'errors')}">
            <input type="text" id="color" name="color" value="${fieldValue(bean: person, field: 'color')}"/>
          </td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">
            <label for="enabled"><g:message code="person.enabled.label" default="Enabled"/>:</label>
          </td>
          <td valign="top" class="value ${hasErrors(bean: person, field: 'enabled', 'errors')}">
            <g:checkBox name="enabled" value="${person?.enabled}"></g:checkBox>
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
