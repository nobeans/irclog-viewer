<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <my:nickStyle persons="${nickPersonList}" classPrefix="" />
    <title><g:message code="summary" default="Summary" /></title>
  </head>
  <body>
    <div class="body summary">
      <my:flashMessage />
      <h1><g:message code="summary" /></h1>
      <div class="caption">
        <g:message code="summary.caption" />
      </div>
      <div class="list">
        <table>
          <thead>
            <tr>
              <th class="searchAllLogs" title="${message(code:'summary.searchAllLogs')}">
                <img src="${createLinkTo(dir:'images',file:'singleTitle.png')}" alt="Search all logs" />
              </th>
              <th class="channel"><g:message code="summary.channel" /></th>
              <% def today = new Date() %>
              <th class="count" title="${my.dateFormat(format:'M/d(E)', value:today)}"><g:message code="summary.today" /></th>
              <th class="count" title="${message(code:'summary.yesterday')}"><my:dateFormat format="M/d(E)" value="${today - 1}" /></th>
              <th class="count" title="${message(code:'summary.twoDaysAgo')}"><my:dateFormat format="M/d(E)" value="${today - 2}" /></th>
              <th class="count" title="${message(code:'summary.threeDaysAgo')}"><my:dateFormat format="M/d(E)" value="${today - 3}" /></th>
              <th class="count" title="${message(code:'summary.fourDaysAgo')}"><my:dateFormat format="M/d(E)" value="${today - 4}" /></th>
              <th class="count" title="${message(code:'summary.fiveDaysAgo')}"><my:dateFormat format="M/d(E)" value="${today - 5}" /></th>
              <th class="count" title="${message(code:'summary.sixDaysAgo')}"><my:dateFormat format="M/d(E)" value="${today - 6}" /></th>
              <th class="totalCount"><g:message code="summary.total" /></th>
              <th class="latestIrclog"><g:message code="summary.latestIrclog" /></th>
            </tr>
          </thead>
          <tbody>
          <g:each in="${channelList}" status="i" var="channel">
            <% def summary = summaryList.find{it.channel == channel} %>
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'} ${(summary) ? '' : 'summaryNotFound'}">
              <td class="searchAllLogs">
                <img class="clickable" src="${createLinkTo(dir:'images', file:'search.png')}" alt="Search all logs" onclick="IRCLOG.goto('${my.searchAllLogsLink(channel:channel)}')" title="${message(code:'summary.searchAllLogs')}" />
              </td>
              <td class="channel">
                <g:link controller="channel" action="show" id="${channel.id}" title="${channel.description}">${fieldValue(bean:channel, field:'name')}</g:link>
              </td>
              <g:if test="${summary}">
                <td class="count"><my:summaryLink count="${summary.today}"        channelName="${channel.name}" time="${summary.lastUpdated}"     /></td>
                <td class="count"><my:summaryLink count="${summary.yesterday}"    channelName="${channel.name}" time="${summary.lastUpdated - 1}" /></td>
                <td class="count"><my:summaryLink count="${summary.twoDaysAgo}"   channelName="${channel.name}" time="${summary.lastUpdated - 2}" /></td>
                <td class="count"><my:summaryLink count="${summary.threeDaysAgo}" channelName="${channel.name}" time="${summary.lastUpdated - 3}" /></td>
                <td class="count"><my:summaryLink count="${summary.fourDaysAgo}"  channelName="${channel.name}" time="${summary.lastUpdated - 4}" /></td>
                <td class="count"><my:summaryLink count="${summary.fiveDaysAgo}"  channelName="${channel.name}" time="${summary.lastUpdated - 5}" /></td>
                <td class="count"><my:summaryLink count="${summary.sixDaysAgo}"   channelName="${channel.name}" time="${summary.lastUpdated - 6}" /></td>
                <td class="totalCount">${summary.total()}</td>
                <td class="latestIrclog" title="${summary?.latestIrclog.message.encodeAsHTML() ?: ''}">
                  <span class="time"><my:dateFormat format="yyyy-MM-dd HH:mm:ss" value="${summary?.latestIrclog.time}" /></span>
                  by <span class="${summary.latestIrclog.nick?.encodeAsHTML()}">${summary?.latestIrclog.nick.encodeAsHTML() ?: ''}</span>
                 </td>
              </g:if>
              <g:else>
                <td class="count">0</td>
                <td class="count">0</td>
                <td class="count">0</td>
                <td class="count">0</td>
                <td class="count">0</td>
                <td class="count">0</td>
                <td class="count">0</td>
                <td class="totalCount">0</td>
                <td class="latestIrclog"></td>
              </g:else>
            </tr>
          </g:each>
          </tbody>
        </table>
      </div>
    </div>
  </body>
</html>
