<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
  <g:set var="entityName" value="${message(code: 'channel.label')}"/>
  <title><g:message code="default.list.label" args="[entityName]"/></title>
  <g:external type="css" uri="${createLink(controller: "dynamicCss", action: "nickColors")}"/>
</head>

<body>
<div class="body channel">
  <irclog:flashMessage/>
  <irclog:withHelp id="channel-list-help">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
  </irclog:withHelp>
  <irclog:help for="channel-list-help">
    <sec:ifLoggedIn><g:message code="channel.list.caption.isLoggedIn"/></sec:ifLoggedIn>
    <sec:ifNotLoggedIn><g:message code="channel.list.caption.isNotLoggedIn"/></sec:ifNotLoggedIn>
  </irclog:help>
  <div class="list">
    <table>
      <thead>
      <tr>
        <th class="searchAllLogs" title="${message(code: 'channel.searchAllLogs.button.label')}">
          <img src="${asset.assetPath(src: 'detailTitle.png')}" alt="Search all logs"/>
        </th>
        <g:sortableColumn property="name" title="Name" titleKey="channel.name.label"/>
        <g:sortableColumn property="description" title="Description" titleKey="channel.description.label"/>
        <g:sortableColumn property="isPrivate" title="Is Private" titleKey="channel.isPrivate.label"/>
        <g:sortableColumn property="isArchived" title="Is Archived" titleKey="channel.isArchived.label"/>
        <th><g:message code="channel.joinedPersons.label"/></th>
      </tr>
      </thead>
      <tbody>
      <g:each in="${channelList}" status="i" var="channel">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
          <td>
            <a href="${irclog.searchAllLogsLink(channel: channel)}" title="${message(code: 'channel.searchAllLogs.button.label')}">
              <img src="${asset.assetPath(src: 'search.png')}" alt="Search all logs"/>
            </a>
          </td>
          <td><g:link action="show" id="${channel.id}">${fieldValue(bean: channel, field: 'name')}</g:link></td>
          <td>${fieldValue(bean: channel, field: 'description')}</td>
          <td><g:message code="channel.isPrivate.label_of.${channel.isPrivate.toString()}"/></td>
          <td><g:message code="channel.isArchived.label_of.${channel.isArchived.toString()}"/></td>
          <td>
            <% channel.persons.sort { it.loginName }.each { person -> %>
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
      <span class="menuButton"><g:link class="create" action="create"><g:message code="default.button.create.label"/></g:link></span>
      <span class="menuButton" id="join"><a id="toggleJoinToSecretChannel" class="hidden" href="javascript:void(0);"><g:message code="channel.join.button.label"/></a></span>
    </div>

    <div id="joinToSecretChannel" style="display:none">
      <g:form action="join" method="post">
        <label for="channelName"><g:message code="channel.name.label"/>:</label>
        <input id="channelName" type="text" name="channelName" value=""/>
        <label for="secretKey"><g:message code="channel.secretKey.label"/>:</label>
        <input id="secretKey" type="password" name="secretKey" value=""/>
        <span class="button"><input type="submit" value="${message(code: "channel.join.submit.button.label")}"/></span>
      </g:form>
    </div>
  </sec:ifLoggedIn>
</div>
</body>
</html>
