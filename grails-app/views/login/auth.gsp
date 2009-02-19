<html>
  <head>
    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>
    <meta name='layout' content='main' />
    <title><g:message code="login" /></title>
    <style type='text/css' media='screen'>
    #login {
      margin: 15px 0px; padding:0px;
      text-align: center;
      font-size: 2em;
    }
    #login .inner {
      width: 500px;
      text-align: left;
      padding: 10px;
      border:1px solid #ccc;
      background: #fff url(../images/slash.png) repeat;
      margin-left: auto;
      margin-right: auto;
    }
    #login .inner .cssform p {
      clear: left;
      padding: 5px 0 5px 10px;
      margin: .4em;
    }
    #login .inner .cssform label {
      font-weight: bold;
    }
    #login .inner .cssform .button {
      text-align: center;
    }
    #login .inner .login_message {color:red;}
    #login .inner .text_ {width: 8em;}
    #login .inner .chk {height:12px;}
    </style>
  </head>
  <body>
    <div id='login'>
      <div class='inner'>
        <g:if test='${flash.message}'>
          <div class='login_message'>${g.message(code:flash.message, args:flash.args, default:flash.defaultMessage)}</div>
        </g:if>
        <form action='${request.contextPath}/j_spring_security_check' method='post' id='loginForm' class='cssform'>
          <p>
            <label for='j_username'><g:message code="person.loginName" />:</label>
            <input type='text' class='text_' name='j_username' id='j_username'  />
          </p>
          <p>
            <label for='j_password'><g:message code="person.password" />:</label>
            <input type='password' class='text_' name='j_password' id='j_password' />
          </p>
          <%--
          <p>
            <input type='checkbox' class='chk' id='remember_me' name='_spring_security_remember_me' />
            <label for='remember_me'><g:message code="login.rememberMe" /></label>
          </p>
          --%>
          <p class="button">
            <input type='submit' value="${message(code:'login')}" />
          </p>
        </form>
      </div>
    </div>
<script type='text/javascript'>
<!--
(function(){
  Event.observe(window, 'load', function() { $('j_username').focus() })
})();
// -->
</script>
  </body>
</html>
