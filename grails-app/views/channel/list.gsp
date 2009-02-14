<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <title><g:message code="channel.list" default="Channel List" /></title>
  </head>
  <body>
    <div class="body channel">
      <my:flashMessage />
      <h1><g:message code="channel.list" default="Channel List" /></h1>
      <div class="caption">
        <g:isLoggedIn><g:message code="channel.list.caption.isLoggedIn" /></g:isLoggedIn>
        <g:isNotLoggedIn><g:message code="channel.list.caption.isNotLoggedIn" /></g:isNotLoggedIn>
      </div>
      <div class="list">
        <table>
          <thead>
            <tr>
              <th class="searchAllLogs" title="${message(code:'channel.searchAllLogs')}">
                <img src="${createLinkTo(dir:'images',file:'singleTitle.png')}" alt="Search all logs" />
              </th>
              <g:sortableColumn property="name" title="Name" titleKey="channel.name" />
              <g:sortableColumn property="description" title="Description" titleKey="channel.description" />
              <g:sortableColumn property="isPrivate" title="Is Private" titleKey="channel.isPrivate" />
              <th><g:message code="channel.joinedPersons" /></th>
            </tr>
          </thead>
          <tbody>
          <g:each in="${channelList}" status="i" var="channel">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
              <td><img class="clickable" src="${createLinkTo(dir:'images', file:'search.png')}" alt="Search all logs" onclick="document.location='${my.searchAllLogsLink(channel:channel)}'" title="${message(code:'channel.searchAllLogs')}" /></td>
              <td><g:link action="show" id="${channel.id}">${fieldValue(bean:channel, field:'name')}</g:link></td>
              <td>${fieldValue(bean:channel, field:'description')}</td>
              <td><g:message code="channel.isPrivate.${channel.isPrivate.toString()}" /></td>
              <td>
                <% allJoinedPersons[channel].each{ person -> %>
                  <span title="${person.realName.encodeAsHTML()}">
                    <g:ifAnyGranted role="ROLE_ADMIN">
                      <g:link controller="person" action="show" id="${person.id}">${person.loginName.encodeAsHTML()}</g:link>
                    </g:ifAnyGranted>
                    <g:ifNotGranted role="ROLE_ADMIN">
                      ${person.loginName.encodeAsHTML()}
                    </g:ifNotGranted>
                  </span>
                <% } %>
              </td>
            </tr>
          </g:each>
          </tbody>
        </table>
      </div>
      <g:isLoggedIn>
        <div class="buttons">
          <span class="menuButton"><g:link class="create" action="create"><g:message code="create" /></g:link></span>
          <span class="menuButton"><a href="javascript:void(0);" onclick="$('joinToSecretChannel').toggle()"><g:message code="channel.join" />...</a></span>
        </div>
        <div id="joinToSecretChannel" style="display:none">
          <g:form action="join" method="post">
            <label for="channelName"><g:message code="channel.name" />:</label>
            <input id="channelName" type="text" name="channelName" value=""/>
            <label for="secretKey"><g:message code="channel.secretKey" />:</label>
            <input id="secretKey" type="password" name="secretKey" value=""/>
            <div class="buttons">
              <span class="button"><input type="submit" value="${message(code:"channel.join.submit")}" /></span>
            </div>
          </g:form>
        </div>
      </g:isLoggedIn>
    </div>
  </body>
</html>
