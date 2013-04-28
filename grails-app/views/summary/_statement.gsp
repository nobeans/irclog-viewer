<div class="summary-statement">
  <irclog:withHelp id="summary-statement-help">
    <h1><g:message code="summary.statement"/></h1>
  </irclog:withHelp>
  <irclog:help for="summary-statement-help">
    <g:message code="summary.statement.caption"/>
  </irclog:help>
  <div class="list">
    <table>
      <thead>
      <tr>
        <th class="searchAllLogs" title="${message(code: 'summary.searchAllLogs')}">
          <img src="${resource(dir: 'images', file: 'detailTitle.png')}" alt="Search all logs"/>
        </th>
        <th class="count">${message(code: 'summary.channel')}</th>
        <% def today = new Date() %>
        <% def dateBefore = { delta -> (today - delta).format('M/d(E)') } %>
        <th class="count">${message(code: 'summary.today')}</th>
        <th class="count">${dateBefore(1)}</th>
        <th class="count">${dateBefore(2)}</th>
        <th class="count">${dateBefore(3)}</th>
        <th class="count">${dateBefore(4)}</th>
        <th class="count">${dateBefore(5)}</th>
        <th class="count">${dateBefore(6)}</th>
        <th class="count">${message(code: 'summary.total')}</th>
        <th class="count">${message(code: 'summary.latestIrclog')}</th>
      </tr>
      </thead>
      <tbody>
      <g:each in="${channelList.sort { it.name }}" status="i" var="channel">
        <% def summary = channel.summary %>
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'} ${(summary) ? '' : 'summaryNotFound'}">
          <td class="searchAllLogs">
            <a href="${irclog.searchAllLogsLink(channel: channel)}" title="${message(code: 'summary.searchAllLogs')}">
              <img src="${resource(dir: 'images', file: 'search.png')}" alt="Search all logs"/>
            </a>
          </td>
          <td class="channel">
            <g:link controller="channel" action="show" id="${channel.id}" title="${channel.description}">${fieldValue(bean: channel, field: 'name')}</g:link>
          </td>
          <td class="count"><irclog:summaryLink count="${summary.today}" channelName="${channel.name}" time="${summary.lastUpdated}"/></td>
          <td class="count"><irclog:summaryLink count="${summary.yesterday}" channelName="${channel.name}" time="${summary.lastUpdated - 1}"/></td>
          <td class="count"><irclog:summaryLink count="${summary.twoDaysAgo}" channelName="${channel.name}" time="${summary.lastUpdated - 2}"/></td>
          <td class="count"><irclog:summaryLink count="${summary.threeDaysAgo}" channelName="${channel.name}" time="${summary.lastUpdated - 3}"/></td>
          <td class="count"><irclog:summaryLink count="${summary.fourDaysAgo}" channelName="${channel.name}" time="${summary.lastUpdated - 4}"/></td>
          <td class="count"><irclog:summaryLink count="${summary.fiveDaysAgo}" channelName="${channel.name}" time="${summary.lastUpdated - 5}"/></td>
          <td class="count"><irclog:summaryLink count="${summary.sixDaysAgo}" channelName="${channel.name}" time="${summary.lastUpdated - 6}"/></td>
          <td class="totalCount">${summary.total}</td>
          <td class="latestIrclog" title="${summary?.latestIrclog?.message?.encodeAsHTML() ?: ''}">
            <g:if test="${summary?.latestIrclog}">
              <span class="time">${summary?.latestIrclog?.time?.format('yyyy-MM-dd HH:mm:ss')}</span>
              (by <span class="${summary.latestIrclog?.nick?.encodeAsHTML() ?: ''}">${summary?.latestIrclog?.nick?.encodeAsHTML() ?: ''}</span>)
            </g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>

