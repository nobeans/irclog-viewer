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
        <th class="count" data-bind="attr: { title: todayLabel }">${message(code: 'summary.today')}</th>
        <th class="count" data-bind="text: yesterdayLabel"></th>
        <th class="count" data-bind="text: twoDaysAgoLabel"></th>
        <th class="count" data-bind="text: threeDaysAgoLabel"></th>
        <th class="count" data-bind="text: fourDaysAgoLabel"></th>
        <th class="count" data-bind="text: fiveDaysAgoLabel"></th>
        <th class="count" data-bind="text: sixDaysAgoLabel"></th>
        <th class="count">${message(code: 'summary.total')}</th>
        <th class="count">${message(code: 'summary.latestIrclog')}</th>
      </tr>
      </thead>
      <tbody data-bind="foreach: summaryList">
      <tr class="summary" data-bind="attr: { id: rowId }">
        <td class="searchAllLogs">
          <a href="#" data-bind="attr: { href: searchAllLogsLink }">
            <img src="${resource(dir: 'images', file: 'search.png')}" alt="Search all logs"/>
          </a>
        </td>
        <td class="channel">
          <a href="#" data-bind="attr: { href: channelLink }, text: channelName"></a>
        </td>
        <td class="count" data-bind="html: todayCount"></td>
        <td class="count" data-bind="html: yesterdayCount"></td>
        <td class="count" data-bind="html: twoDaysAgoCount"></td>
        <td class="count" data-bind="html: threeDaysAgoCount"></td>
        <td class="count" data-bind="html: fourDaysAgoCount"></td>
        <td class="count" data-bind="html: fiveDaysAgoCount"></td>
        <td class="count" data-bind="html: sixDaysAgoCount"></td>
        <td class="totalCount" data-bind="text: totalCount"></td>
        <td class="latestIrclog" data-bind="if: latestMessage, attr: { title: latestMessage }">
          <span class="time" data-bind="text: latestTime"></span>
          (by <span data-bind="text: latestNick, attr: { class: latestNick }"></span>)
        </td>
      </tr>
      </tbody>
    </table>
  </div>
  <input type="hidden" id="token" value="${token}"/>
</div>
