
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <title>Edit Profile</title>
  </head>
  <body>
    <div class="nav">
      <span class="menuButton"><a href="${createLinkTo(dir:'')}">Home</a></span>
    </div>
    <div class="body">
      <h1>Edit Profile</h1>
      <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
      </g:if>
      <g:hasErrors bean="${person}">
        <div class="errors">
          <g:renderErrors bean="${person}" as="list" />
        </div>
      </g:hasErrors>

      <g:form controller="register" method="post" >
        <input type="hidden" name="id" value="${person?.id}" />
        <div class="dialog">
          <table>
            <tbody>
                <tr class='prop'>
                  <td valign='top' class='name'>
                    <label for='loginName'>Login Name:</label>
                  </td>
                  <td valign='top'
                    class='value ${hasErrors(bean:person,field:'loginName','errors')}'>
                  <input type="hidden" name='loginName'
                    value="${person?.loginName?.encodeAsHTML()}"/>
                    <div style="margin:3px">${person?.loginName?.encodeAsHTML()}</div>
                  </td>
                </tr>
                <tr class='prop'>
                  <td valign='top' class='name'>
                    <label for='password'>Password:</label>
                  </td>
                  <td valign='top'
                    class='value ${hasErrors(bean:person,field:'password','errors')}'>
                    <input type="password" name='password' value=""/>
                  </td>
                </tr>
                <tr class='prop'>
                  <td valign='top' class='name'>
                    <label for='enabled'>Confirm Password:</label>
                  </td>
                  <td valign='top'
                    class='value ${hasErrors(bean:person,field:'password','errors')}'>
                    <input type="password" name='repassword'
                      value=""/>
                  </td>
                </tr>
            </tbody>
          </table>
        </div>

        <div class="buttons">
          <span class="button"><g:actionSubmit value="Update" /></span>
        </div>

      </g:form>

    </div>
  </body>
</html>
