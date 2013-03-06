<div class="condition caption">
  <select id="condition-channel" name="channel" data-bind="options: channelList, optionsText: 'name', optionsValue: 'shortName', value: currentChannel, event: {change: changeChannel}"></select>
  at
  <span id="condition-date">
    <span data-bind="if: showBeforeDate()"><g:img class="button" file="singleBefore.png" data-bind="event: {click: toBeforeDate}" title="${message(code:'singleViewer.beforeDate')}"/></span>
    <span id="currentDate" data-bind="text: currentDate">${params.date}</span>
    <span data-bind="if: showAfterDate()"><g:img class="button" file="singleAfter.png" data-bind="event: {click: toAfterDate}" title="${message(code:'singleViewer.afterDate')}"/></span>
    <span data-bind="if: showLatestDate()"><g:img class="button" file="singleLatest.png" data-bind="event: {click: toLatestDate}" title="${message(code:'singleViewer.latestDate')}"/></span>
    <input type="text" class="datepicker hidden" value="${criterion['period-oneday-date']}"/>
  </span>
</div>