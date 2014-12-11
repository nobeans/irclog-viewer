<div class="condition caption">
  <select id="condition-channel" name="channel" data-bind="options: channelList, optionsText: 'name', optionsValue: 'shortName', value: currentChannel, event: {change: changeChannel}"></select>
  at
  <span id="condition-date">
    <span data-bind="if: showBeforeDate()"><asset:image class="button" src="detailBefore.png" data-bind="event: {click: toBeforeDate}" title="${message(code: 'detail.beforeDate')}"/></span>
    <span id="currentDate" data-bind="text: currentDate">${params.date}</span>
    <span data-bind="if: showAfterDate()"><asset:image class="button" src="detailAfter.png" data-bind="event: {click: toAfterDate}" title="${message(code: 'detail.afterDate')}"/></span>
    <span data-bind="if: showLatestDate()"><asset:image class="button" src="detailLatest.png" data-bind="event: {click: toLatestDate}" title="${message(code: 'detail.latestDate')}"/></span>
    <input type="text" class="datepicker hidden" value="${criterion['periodOnedayDate']}"/>
  </span>
</div>
