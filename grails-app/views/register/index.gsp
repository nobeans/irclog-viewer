
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <title>User Registration</title>         
  </head>
  <body>
    <div class="nav">
      <span class="menuButton"><a href="${createLinkTo(dir:'')}">Home</a></span>
    </div>
    <div class="body">
      <h1>User Registration</h1>
      <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
      </g:if>
      <g:hasErrors bean="${person}">
        <div class="errors">
          <g:renderErrors bean="${person}" as="list" />
        </div>
      </g:hasErrors>
      <g:form action="save" method="post" >
        <div class="dialog">
          <table>
            <tbody>

              <tr class='prop'>
                <td valign='top' class='name'>
                  <label for='loginName'>Login Name:</label>
                </td>
                <td valign='top' 
                	class='value ${hasErrors(bean:person,field:'loginName','errors')}'>
                  <input type="text" name='loginName' 
                  		 value="${person?.loginName?.encodeAsHTML()}"/>
                </td>
              </tr>
                       
              <tr class='prop'>
                <td valign='top' class='name'>
                  <label for='password'>Password:</label>
                </td>
                <td valign='top' 
                    class='value ${hasErrors(bean:person,field:'password','errors')}'>
                  <input type="password" name='password' 
                         value="${person?.password?.encodeAsHTML()}"/>
                </td>
              </tr>
                       
              <tr class='prop'>
                <td valign='top' class='name'>
                  <label for='enabled'>Confirm Password:</label>
                </td>
                <td valign='top' 
                	class='value ${hasErrors(bean:person,field:'password','errors')}'>
                  <input type="password" name='repassword' 
                         value="${person?.password?.encodeAsHTML()}"/>
                </td>
              </tr>
                       
            </tbody>
          </table>
        </div>
        
        <div class="buttons">
          <span class="formButton">
            <input type="submit" value="Create"></input>
          </span>
        </div>
            
      </g:form>
    </div>
  </body>
</html>
