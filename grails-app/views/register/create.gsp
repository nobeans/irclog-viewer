<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
  <g:set var="entityName" value="${message(code: 'register.label')}"/>
  <title><g:message code="default.create.label" args="[entityName]"/></title>
</head>
<html>

<body>
<div class="body">
  <irclog:flashMessage bean="${person}"/>
  <h1><g:message code="default.create.label" args="[entityName]"/></h1>
  <g:form action="save" method="post">
    <div class="dialog">
      <table>
        <tbody>
        <tr class='prop'>
          <td valign='top' class='name'>
            <label for='loginName'><g:message code="person.loginName.label"/>:</label>
          </td>
          <td valign='top' class='value ${hasErrors(bean: person, field: 'loginName', 'errors')}'>
            <input type="text" name='loginName' value="${person?.loginName?.encodeAsHTML()}"/>
          </td>
          <td class="caption"><g:message code="person.loginName.caption"/></td>
        </tr>
        <tr class='prop'>
          <td valign='top' class='name'>
            <label for='realName'><g:message code="person.realName.label"/>:</label>
          </td>
          <td valign='top' class='value ${hasErrors(bean: person, field: 'realName', 'errors')}'>
            <input type="text" name='realName' value="${person?.realName?.encodeAsHTML()}"/>
          </td>
          <td class="caption"><g:message code="person.realName.caption"/></td>
        </tr>
        <tr class='prop'>
          <td valign='top' class='name'>
            <label for='password'><g:message code="person.password.label"/>:</label>
          </td>
          <td valign='top' class='value ${hasErrors(bean: person, field: 'password', 'errors')}'>
            <input type="password" name='password' value="${person?.password?.encodeAsHTML()}"/>
          </td>
          <td class="caption"></td>
        </tr>
        <tr class='prop'>
          <td valign='top' class='name'>
            <label for='repassword'><g:message code="person.repassword.label"/>:</label>
          </td>
          <td valign='top' class='value ${hasErrors(bean: person, field: 'password', 'errors')}'>
            <input type="password" name='repassword' value="${person?.repassword?.encodeAsHTML()}"/>
          </td>
          <td class="caption"></td>
        </tr>
        <tr class='prop'>
          <td valign='top' class='name'>
            <label for='nicks'><g:message code="person.nicks.label"/>:</label>
          </td>
          <td valign='top' class='value ${hasErrors(bean: person, field: 'nicks', 'errors')}'>
            <input type="nicks" name='nicks' value="${person?.nicks?.encodeAsHTML()}"/>
          </td>
          <td class="caption"><g:message code="person.nicks.caption"/></td>
        </tr>
        <tr class='prop'>
          <td valign='top' class='name'>
            <label for='color'><g:message code="person.color.label"/>:</label>
          </td>
          <td valign='top' class='value ${hasErrors(bean: person, field: 'color', 'errors')}'>
            <input type="color" name='color' value="${person?.color?.encodeAsHTML()}"/>
          </td>
          <td class="caption"><g:message code="person.color.caption"/></td>
        </tr>
        </tbody>
      </table>
    </div>

    <div class="buttons">
      <span class="button"><input class="save" type="submit" value="${message(code: 'default.button.create.label')}"/></span>
    </div>
  </g:form>
</div>
</body>
</html>
