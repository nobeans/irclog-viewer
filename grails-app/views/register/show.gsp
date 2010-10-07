  
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <title>${message(code:'register.show')}</title>
  </head>
  <body>
    <div class="body">
      <my:flashMessage bean="${person}" />
      <h1><g:message code="register.show" /></h1>
      <div class="dialog">
        <table>
          <tbody>
            <tr class="prop">
              <td valign='top' class='name'>
                <g:message code="person.loginName" />:
              </td>
              <td valign="top" class="value">
              	${person.loginName?.encodeAsHTML()}
              </td>
            </tr>
            <tr class="prop">
              <td valign='top' class='name'>
                <g:message code="person.realName" />:
              </td>
              <td valign="top" class="value">
              	${person.realName?.encodeAsHTML()}
              </td>
            </tr>
            <tr class="prop">
              <td valign="top" class="name">
                <g:message code="person.nicks" />:
              </td>
              <td valign="top" class="value">
                ${person.nicks?.encodeAsHTML()}
              </td>
            </tr>
            <tr class="prop">
              <td valign="top" class="name">
                <g:message code="person.color" />:
              </td>
              <td valign="top" class="value" style="color:${person.color?.encodeAsHTML()}">
                ${person.color?.encodeAsHTML()}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="buttons">
        <g:form action="edit">
          <input type="hidden" name="id" value="${person?.id}" />
          <span class="button"><input type="submit" class="edit" value="${message(code:'edit')}" /></span>
        </g:form>
      </div>
    </div>
  </body>
</html>
