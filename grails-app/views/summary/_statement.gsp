<div class="summary-statement">
  <my:withHelp id="summary-statement-help">
    <h1><g:message code="summary.statement" /></h1>
  </my:withHelp>
  <my:help for="summary-statement-help">
    <g:message code="summary.statement.caption" />
  </my:help>
  <div class="list">
    <table>
      <thead>
        <tr>
          <th class="searchAllLogs" title="${message(code:'summary.searchAllLogs')}">
            <img src="${resource(dir:'images',file:'singleTitle.png')}" alt="Search all logs" />
          </th>
          <g:sortableColumn action="index" defaultOrder="asc" property="channel.name" titleKey="summary.channel" />
          <% def today = new Date() %>
          <% def dateBefore = { delta -> (today - delta).format('M/d(E)') } %>
          <g:sortableColumn class="count" action="index" defaultOrder="desc" property="today" titleKey="summary.today" />
          <g:sortableColumn class="count" action="index" defaultOrder="desc" property="yesterday" title="${dateBefore(1)}"/>
          <g:sortableColumn class="count" action="index" defaultOrder="desc" property="twoDaysAgo" title="${dateBefore(2)}"/>
          <g:sortableColumn class="count" action="index" defaultOrder="desc" property="threeDaysAgo" title="${dateBefore(3)}"/>
          <g:sortableColumn class="count" action="index" defaultOrder="desc" property="fourDaysAgo" title="${dateBefore(4)}"/>
          <g:sortableColumn class="count" action="index" defaultOrder="desc" property="fiveDaysAgo" title="${dateBefore(5)}"/>
          <g:sortableColumn class="count" action="index" defaultOrder="desc" property="sixDaysAgo" title="${dateBefore(6)}"/>
          <g:sortableColumn class="count" action="index" defaultOrder="desc" property="total" titleKey="summary.total" />
          <g:sortableColumn class="latestIrclog" action="index" defaultOrder="desc" property="latestIrclog" titleKey="summary.latestIrclog" />
        </tr>
      </thead>
      <tbody>
      <g:each in="${summaryList}" status="i" var="summary">
        <% def channel = summary.channel %>
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'} ${(summary) ? '' : 'summaryNotFound'}">
          <td class="searchAllLogs">
            <a href="${my.searchAllLogsLink(channel:channel)}" title="${message(code:'summary.searchAllLogs')}">
              <img src="${resource(dir:'images', file:'search.png')}" alt="Search all logs" />
            </a>
          </td>
          <td class="channel">
            <g:link controller="channel" action="show" id="${channel.id}" title="${channel.description}">${fieldValue(bean:channel, field:'name')}</g:link>
          </td>
          <td class="count"><my:summaryLink count="${summary.today}"        channelName="${channel.name}" time="${summary.lastUpdated}"     /></td>
          <td class="count"><my:summaryLink count="${summary.yesterday}"    channelName="${channel.name}" time="${summary.lastUpdated - 1}" /></td>
          <td class="count"><my:summaryLink count="${summary.twoDaysAgo}"   channelName="${channel.name}" time="${summary.lastUpdated - 2}" /></td>
          <td class="count"><my:summaryLink count="${summary.threeDaysAgo}" channelName="${channel.name}" time="${summary.lastUpdated - 3}" /></td>
          <td class="count"><my:summaryLink count="${summary.fourDaysAgo}"  channelName="${channel.name}" time="${summary.lastUpdated - 4}" /></td>
          <td class="count"><my:summaryLink count="${summary.fiveDaysAgo}"  channelName="${channel.name}" time="${summary.lastUpdated - 5}" /></td>
          <td class="count"><my:summaryLink count="${summary.sixDaysAgo}"   channelName="${channel.name}" time="${summary.lastUpdated - 6}" /></td>
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

