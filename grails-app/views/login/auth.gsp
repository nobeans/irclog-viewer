<html>
  <head>
    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>
    <meta name='layout' content='main' />
    <title><g:message code="login" /></title>
<style type='text/css' media='screen'>
#login {
  margin:15px 0px; padding:0px;
  text-align:center;
}
#login .inner {
  width:300px;
  text-align: left;
  padding: 10px;
  border:1px solid #ccc;
  background-color:#eee;
  margin-left: auto;
  margin-right: auto;
}
#login .inner .cssform p {
  clear: left;
  padding: 5px 0 5px 10px;
}
#login .inner .cssform label {
  font-weight: bold;
}
#login .inner .login_message {color:red;}
#login .inner .text_ {width:120px;}
#login .inner .chk {height:12px;}
</style>
  </head>
  <body>
    <div id='login'>
      <div class='inner'>
        <g:if test='${flash.message}'>
          <div class='login_message'>${g.message(code:flash.message, args:flash.args, default:flash.defaultMessage)}</div>
        </g:if>
        <form action='${request.contextPath}/j_spring_security_check' method='POST' id='loginForm' class='cssform'>
          <p>
            <label for='j_username'><g:message code="person.loginName" /></label>
            <input type='text' class='text_' name='j_username' id='j_username'  />
          </p>
          <p>
            <label for='j_password'><g:message code="person.password" /></label>
            <input type='password' class='text_' name='j_password' id='j_password' />
          </p>
          <p>
            <input type='checkbox' class='chk' id='remember_me' name='_spring_security_remember_me' />
            <label for='remember_me'><g:message code="login.rememberMe" /></label>
          </p>
          <p>
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
