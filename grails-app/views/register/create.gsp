<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <title>${message(code:'register.create')}</title>
  </head>
  <body>
    <div class="body">
      <my:flashMessage bean="${person}" />
      <h1><g:message code="register.create" /></h1>
      <g:form action="save" method="post" >
        <div class="dialog">
          <table>
            <tbody>
              <tr class='prop'>
                <td valign='top' class='name'>
                  <label for='loginName'><g:message code="person.loginName" />:</label>
                </td>
                <td valign='top' class='value ${hasErrors(bean:person,field:'loginName','errors')}'>
                  <input type="text" name='loginName' value="${person?.loginName?.encodeAsHTML()}"/>
                </td>
              </tr>
              <tr class='prop'>
                <td valign='top' class='name'>
                  <label for='password'><g:message code="person.password" />:</label>
                </td>
                <td valign='top' class='value ${hasErrors(bean:person,field:'password','errors')}'>
                  <input type="password" name='password' value="${person?.password?.encodeAsHTML()}"/>
                </td>
              </tr>
              <tr class='prop'>
                <td valign='top' class='name'>
                  <label for='repassword'><g:message code="person.repassword" />:</label>
                </td>
                <td valign='top' class='value ${hasErrors(bean:person,field:'password','errors')}'>
                  <input type="password" name='repassword' value="${person?.repassword?.encodeAsHTML()}"/>
                </td>
              </tr>
              <tr class='prop'>
                <td valign='top' class='name'>
                  <label for='nicks'><g:message code="person.nicks" />:</label>
                </td>
                <td valign='top' class='value ${hasErrors(bean:person,field:'nicks','errors')}'>
                  <input type="nicks" name='nicks' value="${person?.nicks?.encodeAsHTML()}"/>
                </td>
              </tr>
              <tr class='prop'>
                <td valign='top' class='name'>
                  <label for='color'><g:message code="person.color" />:</label>
                </td>
                <td valign='top' class='value ${hasErrors(bean:person,field:'color','errors')}'>
                  <input type="color" name='color' value="${person?.color?.encodeAsHTML()}"/>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="buttons">
          <span class="button"><input type="submit" class="save" value="${message(code:'create')}" /></span>
        </div>
      </g:form>
    </div>
  </body>
</html>