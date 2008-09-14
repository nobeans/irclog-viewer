<div class="search">
  <g:form method="get">
    <input class="image" type="image" src="${createLinkTo(dir:'images',file:'search.png')}" title="${message(code:'viewer.search.button.tooltips')}" />
    <label for="search-period"><g:message code="viewer.search.period" />:</label>
      <g:select id="search-period" name="period" from="${selectablePeriods}" value="${criterion?.period}" valueMessagePrefix="viewer.search.period" />
      <span id="period-oneday-select" style="display:none">
        <my:calendar name="period-oneday-date" value="${criterion?.'period-oneday-date'}" title="${message(code:'viewer.search.period.oneday.tooltips')}"/>
      </span>
    <label for="search-channel"><g:message code="viewer.search.channel" />:</label>
      <g:select id="search-channel" name="channel" from="${selectableChannels}" value="${criterion?.channel}" optionKey="key" optionValue="value" />
    <label for="search-nick"><g:message code="viewer.search.nick" />:</label>
      <input id="search-nick" type="text" name="nick" value="${criterion?.nick}"></input>
    <label for="search-message"><g:message code="viewer.search.message" />:</label>
      <input id="search-message" type="text" name="message" value="${criterion?.message}"></input>
    <label for="search-type"><g:message code="viewer.search.type" />:</label>
      <g:select id="search-type" name="type" from="${Irclog.typeList}" value="${criterion?.type}" valueMessagePrefix="viewer.search.type" />
  </g:form>
</div>
