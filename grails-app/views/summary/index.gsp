<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <g:javascript library="common" />
    <title><g:message code="summary" default="Summary" /></title>
  </head>
  <body>
    <div class="body summary">
      <my:flashMessage />
      <h1><g:message code="summary" /></h1>
      <div class="list">
        <table>
          <thead>
            <tr>
              <th class="searchAllLogs" title="${message(code:'channel.searchAllLogs')}">
                <img src="${createLinkTo(dir:'images',file:'singleTitle.png')}" alt="Search all logs" />
              </th>
              <th><g:message code="summary.channel" /></th>
              <th><g:message code="summary.today" /></th>
              <th><g:message code="summary.yesterday" /></th>
              <th><g:message code="summary.twoDaysAgo" /></th>
              <th><g:message code="summary.threeDaysAgo" /></th>
              <th><g:message code="summary.fourDaysAgo" /></th>
              <th><g:message code="summary.fiveDaysAgo" /></th>
              <th><g:message code="summary.sixDaysAgo" /></th>
              <th><g:message code="summary.total" /></th>
              <th><g:message code="summary.latestTime" /></th>
            </tr>
          </thead>
          <tbody>
          <g:each in="${channelList}" status="i" var="channel">
            <% def summary = summaryList.find{it.channel == channel} %>
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
              <td><img class="clickable" src="${createLinkTo(dir:'images', file:'search.png')}" alt="Search all logs" onclick="IRCLOG.goto('${my.searchAllLogsLink(channel:channel).encodeAsHTML()}')" title="${message(code:'channel.searchAllLogs')}" /></td>
              <td>${channel.name}</td>
              <g:if test="${summary}">
                <td><my:summaryLink count="${summary.today}" channelName="${channel.name}" time="${summary.baseDate}" /></td>
                <td><my:summaryLink count="${summary.yesterday}" channelName="${channel.name}" time="${summary.baseDate - 1}" /></td>
                <td><my:summaryLink count="${summary.twoDaysAgo}" channelName="${channel.name}" time="${summary.baseDate - 1}" /></td>
                <td><my:summaryLink count="${summary.threeDaysAgo}" channelName="${channel.name}" time="${summary.baseDate - 1}" /></td>
                <td><my:summaryLink count="${summary.fourDaysAgo}" channelName="${channel.name}" time="${summary.baseDate - 1}" /></td>
                <td><my:summaryLink count="${summary.fiveDaysAgo}" channelName="${channel.name}" time="${summary.baseDate - 1}" /></td>
                <td><my:summaryLink count="${summary.sixDaysAgo}" channelName="${channel.name}" time="${summary.baseDate - 1}" /></td>
                <td>${summary.total()}</td>
                <td><my:dateFormat format="yyyy-MM-dd HH:mm:ss" value="${summary?.latestTime}" /></td>
              </g:if>
              <g:else>
                <td>0</td>
                <td>0</td>
                <td>0</td>
                <td>0</td>
                <td>0</td>
                <td>0</td>
                <td>0</td>
                <td>0</td>
                <td></td>
              </g:else>
            </tr>
          </g:each>
          </tbody>
        </table>
      </div>
    </div>
  </body>
</html>
