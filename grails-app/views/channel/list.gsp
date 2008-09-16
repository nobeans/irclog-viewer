<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <title><g:message code="channel.list" default="Channel List" /></title>
  </head>
  <body>
    <div class="body">
      <h1><g:message code="channel.list" default="Channel List" /></h1>
      <g:if test="${flash.message}">
      <div class="message"><g:message code="${flash.message}" args="${flash.args}" default="${flash.defaultMessage}" /></div>
      </g:if>
      <div class="list">
        <table>
          <thead>
            <tr>
              <g:sortableColumn property="name" title="Name" titleKey="channel.name" />
              <g:sortableColumn property="description" title="Description" titleKey="channel.description" />
              <g:sortableColumn property="isPrivate" title="Is Private" titleKey="channel.isPrivate" />
            </tr>
          </thead>
          <tbody>
          <g:each in="${channelList}" status="i" var="channel">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
              <td><g:link action="show" id="${channel.id}">${fieldValue(bean:channel, field:'name')}</g:link></td>
              <td>${fieldValue(bean:channel, field:'description')}</td>
              <td><g:message code="channel.isPrivate.${channel.isPrivate.toString()}" /></td>
            </tr>
          </g:each>
          </tbody>
        </table>
      </div>
      <div class="buttons">
        <span class="menuButton"><g:link class="create" action="create"><g:message code="channel.new" /></g:link></span>
        <span class="menuButton"><a href="javascript:void(0);" onclick="$('joinToSecretChannel').toggle()"><g:message code="channel.join" /></a></span>
      </div>
      <div id="joinToSecretChannel" style="display:none">
        <h3><g:message code="channel.join" /></h3>
        <div class="buttons">
          <span class="menuButton"><g:link class="join" action="join"><g:message code="channel.join" /></g:link></span>
        </div>
      </div>
    </div>
  </body>
</html>
