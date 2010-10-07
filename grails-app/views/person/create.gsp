<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <title><g:message code="person.create" default="Create Person" /></title>
  </head>
  <body>
    <div class="body">
      <my:flashMessage bean="${person}" />
      <h1><g:message code="person.create" default="Create Person" /></h1>
      <g:form action="save" method="post" >
        <div class="dialog">
          <table>
            <tbody>
              <tr class="prop">
                <td valign="top" class="name">
                  <label for="loginName"><g:message code="person.loginName" default="Login Name" />:</label>
                </td>
                <td valign="top" class="value ${hasErrors(bean:person,field:'loginName','errors')}">
                  <input type="text" id="loginName" name="loginName" value="${fieldValue(bean:person,field:'loginName')}"/>
                </td>
              </tr> 
              <tr class="prop">
                <td valign="top" class="name">
                  <label for="realName"><g:message code="person.realName" default="Real Name" />:</label>
                </td>
                <td valign="top" class="value ${hasErrors(bean:person,field:'realName','errors')}">
                  <input type="text" maxlength="100" id="realName" name="realName" value="${fieldValue(bean:person,field:'realName')}"/>
                </td>
              </tr> 
              <tr class="prop">
                <td valign="top" class="name">
                  <label for="password"><g:message code="person.password" default="Password" />:</label>
                </td>
                <td valign="top" class="value ${hasErrors(bean:person,field:'password','errors')}">
                  <input type="password" id="password" name="password" value="${fieldValue(bean:person,field:'password')}"/>
                </td>
              </tr> 
              <tr class="prop">
                <td valign="top" class="name">
                  <label for="repassword"><g:message code="person.repassword" default="Repassword" />:</label>
                </td>
                <td valign="top" class="value ${hasErrors(bean:person,field:'password','errors')}">
                  <input type="password" name="repassword" id="repassword" value="${fieldValue(bean:person,field:'repassword')}" />
                </td>
              </tr> 
              <tr class="prop">
                <td valign="top" class="name">
                  <label for="nicks"><g:message code="person.nicks" default="Nicks" />:</label>
                </td>
                <td valign="top" class="value ${hasErrors(bean:person,field:'nicks','errors')}">
                  <input type="text" id="nicks" name="nicks" value="${fieldValue(bean:person,field:'nicks')}"/>
                </td>
              </tr> 
              <tr class="prop">
                <td valign="top" class="name">
                  <label for="color"><g:message code="person.color" default="Color" />:</label>
                </td>
                <td valign="top" class="value ${hasErrors(bean:person,field:'color','errors')}">
                  <input type="text" id="color" name="color" value="${fieldValue(bean:person,field:'color')}"/>
                </td>
              </tr> 
              <tr class="prop">
                <td valign="top" class="name">
                  <label for="enabled"><g:message code="person.enabled" default="Enabled" />:</label>
                </td>
                <td valign="top" class="value ${hasErrors(bean:person,field:'enabled','errors')}">
                  <g:checkBox name="enabled" value="${person?.enabled}" ></g:checkBox>
                </td>
              </tr> 
            </tbody>
          </table>
        </div>
        <div class="buttons">
          <span class="button"><input class="save" type="submit" value="${message(code:'create', 'default':'Create')}" /></span>
        </div>
      </g:form>
    </div>
  </body>
</html>
