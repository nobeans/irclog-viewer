<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <title><g:message code="channel.list" default="Channel List" /></title>
    <my:nickStyle persons="${nickPersonList}" classPrefix=".channel " />
    <r:require module="channel" />
  </head>
  <body>
    <div class="body channel">
      <my:flashMessage />
      <my:withHelp id="channel-list-help">
        <h1><g:message code="channel.list" default="Channel List" /></h1>
      </my:withHelp>
      <my:help for="channel-list-help">
        <sec:ifLoggedIn><g:message code="channel.list.caption.isLoggedIn" /></sec:ifLoggedIn>
        <sec:ifNotLoggedIn><g:message code="channel.list.caption.isNotLoggedIn" /></sec:ifNotLoggedIn>
      </my:help>
      <div class="list">
        <table>
          <thead>
            <tr>
              <th class="searchAllLogs" title="${message(code:'channel.searchAllLogs')}">
                <img src="${resource(dir:'images',file:'singleTitle.png')}" alt="Search all logs" />
              </th>
              <g:sortableColumn property="name" title="Name" titleKey="channel.name" />
              <g:sortableColumn property="description" title="Description" titleKey="channel.description" />
              <g:sortableColumn property="isPrivate" title="Is Private" titleKey="channel.isPrivate" />
              <g:sortableColumn property="isArchived" title="Is Archived" titleKey="channel.isArchived" />
              <th><g:message code="channel.joinedPersons" /></th>
            </tr>
          </thead>
          <tbody>
          <g:each in="${channelList}" status="i" var="channel">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
              <td>
                <a href="${my.searchAllLogsLink(channel:channel)}" title="${message(code:'channel.searchAllLogs')}">
                  <img src="${resource(dir:'images', file:'search.png')}" alt="Search all logs" />
                </a>
              </td>
              <td><g:link action="show" id="${channel.id}">${fieldValue(bean:channel, field:'name')}</g:link></td>
              <td>${fieldValue(bean:channel, field:'description')}</td>
              <td><g:message code="channel.isPrivate.${channel.isPrivate.toString()}" /></td>
              <td><g:message code="channel.isArchived.${channel.isArchived.toString()}" /></td>
              <td>
                <% allJoinedPersons[channel].each{ person -> %>
                  <span class="${person.loginName}" title="${person.realName.encodeAsHTML()}">
                    <sec:ifAnyGranted roles="ROLE_ADMIN">
                      <g:link controller="person" action="show" id="${person.id}">${person.loginName.encodeAsHTML()}</g:link>
                    </sec:ifAnyGranted>
                    <sec:ifNotGranted roles="ROLE_ADMIN">
                      ${person.loginName.encodeAsHTML()}
                    </sec:ifNotGranted>
                  </span>
                <% } %>
              </td>
            </tr>
          </g:each>
          </tbody>
        </table>
      </div>
      <sec:ifLoggedIn>
        <div class="buttons">
          <span class="menuButton"><g:link class="create" action="create"><g:message code="create" /></g:link></span>
          <span class="menuButton" id="join"><a id="toggleJoinToSecretChannel" href="javascript:void(0);"><g:message code="channel.join" />...</a></span>
        </div>
        <div id="joinToSecretChannel" style="display:none">
          <g:form action="join" method="post">
            <label for="channelName"><g:message code="channel.name" />:</label>
            <input id="channelName" type="text" name="channelName" value=""/>
            <label for="secretKey"><g:message code="channel.secretKey" />:</label>
            <input id="secretKey" type="password" name="secretKey" value=""/>
            <span class="button"><input type="submit" value="${message(code:"channel.join.submit")}" /></span>
          </g:form>
        </div>
      </sec:ifLoggedIn>
    </div>
  </body>
</html>
