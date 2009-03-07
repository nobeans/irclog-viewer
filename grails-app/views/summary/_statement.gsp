<div class="summary-statement">
  <my:withHelp id="summary-statement-caption">
    <h1><g:message code="summary.statement" /></h1>
  </my:withHelp>
  <my:help for="summary-statement-caption">
    <g:message code="summary.statement.caption" />
  </my:help>
  <div class="list">
    <table>
      <thead>
        <tr>
          <th class="searchAllLogs" title="${message(code:'summary.searchAllLogs')}">
            <img src="${createLinkTo(dir:'images',file:'singleTitle.png')}" alt="Search all logs" />
          </th>
          <my:sortableColumn action="index" defaultOrder="asc" property="channel" code="summary.channel" />
          <g:if test="${session.timeMarker}">
            <my:sortableColumn class="count todayAfterTimeMarker" action="index" defaultOrder="desc" property="todayAfterTimeMarker" titleKey="summary.todayAfterTimeMarker.tooltips" >
              <img src="${createLinkTo(dir:'images', file:'clock.png')}" alt="Clock" />
            </my:sortableColumn>
          </g:if>
          <% def today = new Date() %>
          <% def dateBefore = { delta -> my.dateFormat(format:'M/d(E)', value:(today - delta))} %>
          <my:sortableColumn class="count" action="index" defaultOrder="desc" property="today" code="summary.today" title="${dateBefore(0)}" />
          <my:sortableColumn class="count" action="index" defaultOrder="desc" property="yesterday">${dateBefore(1)}</my:sortableColumn>
          <my:sortableColumn class="count" action="index" defaultOrder="desc" property="twoDaysAgo">${dateBefore(2)}</my:sortableColumn>
          <my:sortableColumn class="count" action="index" defaultOrder="desc" property="threeDaysAgo">${dateBefore(3)}</my:sortableColumn>
          <my:sortableColumn class="count" action="index" defaultOrder="desc" property="fourDaysAgo">${dateBefore(4)}</my:sortableColumn>
          <my:sortableColumn class="count" action="index" defaultOrder="desc" property="fiveDaysAgo">${dateBefore(5)}</my:sortableColumn>
          <my:sortableColumn class="count" action="index" defaultOrder="desc" property="sixDaysAgo">${dateBefore(6)}</my:sortableColumn>
          <my:sortableColumn class="count" action="index" defaultOrder="desc" property="total" code="summary.total" titleKey="summary.total.tooltips" />
          <my:sortableColumn class="latestIrclog" action="index" defaultOrder="desc" property="latestIrclog" code="summary.latestIrclog" titleKey="summary.latestIrclog.tooltips" />
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
          <g:if test="${session.timeMarker}">
            <td class="count todayAfterTimeMarker">
              <my:summaryLink count="${summary.todayAfterTimeMarker}" channelName="${channel.name}" time="${summary.lastUpdated}" timeMarkerJump="${true}" />
            </td>
          </g:if>
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

