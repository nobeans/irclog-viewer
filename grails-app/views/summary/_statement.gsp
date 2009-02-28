<div class="summary-statement">
  <h1><g:message code="summary.statement" /></h1>
  <div class="caption">
    <g:message code="summary.statement.caption" />
  </div>
  <div class="list">
    <table>
      <thead>
        <tr>
          <th class="searchAllLogs" title="${message(code:'summary.searchAllLogs')}">
            <img src="${createLinkTo(dir:'images',file:'singleTitle.png')}" alt="Search all logs" />
          </th>
          <g:sortableColumn property="channel" titleKey="summary.channel" action="index" />
          <% def today = new Date() %>
          <g:sortableColumn property="today"        class="count" titleKey="summary.today" />
          <g:sortableColumn property="yesterday"    class="count" title="${my.dateFormat(format:'M/d(E)', value:today - 1)}" />
          <g:sortableColumn property="twoDaysAgo"   class="count" title="${my.dateFormat(format:'M/d(E)', value:today - 2)}" />
          <g:sortableColumn property="threeDaysAgo" class="count" title="${my.dateFormat(format:'M/d(E)', value:today - 3)}" />
          <g:sortableColumn property="fourDaysAgo"  class="count" title="${my.dateFormat(format:'M/d(E)', value:today - 4)}" />
          <g:sortableColumn property="fiveDaysAgo"  class="count" title="${my.dateFormat(format:'M/d(E)', value:today - 5)}" />
          <g:sortableColumn property="sixDaysAgo"   class="count" title="${my.dateFormat(format:'M/d(E)', value:today - 6)}" />
          <g:sortableColumn property="total"        class="count" titleKey="summary.total" />
          <g:sortableColumn property="latestIrclog" class="latestIrclog" titleKey="summary.latestIrclog" />
        </tr>
      </thead>
      <tbody>
      <g:each in="${summaryList}" status="i" var="summary">
        <% def channel = summary.channel %>
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'} ${(summary) ? '' : 'summaryNotFound'}">
          <td class="searchAllLogs">
            <img class="clickable" src="${createLinkTo(dir:'images', file:'search.png')}" alt="Search all logs" onclick="IRCLOG.goto('${my.searchAllLogsLink(channel:channel)}')" title="${message(code:'summary.searchAllLogs')}" />
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
          <td class="totalCount">${summary.total()}</td>
          <td class="latestIrclog" title="${summary?.latestIrclog?.message?.encodeAsHTML() ?: ''}">
            <g:if test="${summary?.latestIrclog}">
              <span class="time"><my:dateFormat format="yyyy-MM-dd HH:mm:ss" value="${summary?.latestIrclog?.time}" /></span>
              (by <span class="${summary.latestIrclog?.nick?.encodeAsHTML() ?: ''}">${summary?.latestIrclog?.nick?.encodeAsHTML() ?: ''}</span>)
            </g:if>
           </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>
