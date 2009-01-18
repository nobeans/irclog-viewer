<div class="search">
  <g:form method="get">
    <label for="search-period"><g:message code="mixedViewer.search.period" />:</label>
      <g:select id="search-period" name="period" from="${selectablePeriods}" value="${criterion?.period}" valueMessagePrefix="mixedViewer.search.period" />
      <span id="period-oneday-select" style="display:none">
        <my:calendar name="period-oneday-date" value="${criterion?.'period-oneday-date'}" title="${message(code:'mixedViewer.search.period.oneday.tooltips')}"/>
      </span>
    <label for="search-channel"><g:message code="mixedViewer.search.channel" />:</label>
      <g:select id="search-channel" name="channel" from="${selectableChannels}" value="${criterion?.channel}" optionKey="key" optionValue="value" />
    <label for="search-nick"><g:message code="mixedViewer.search.nick" />:</label>
      <input id="search-nick" type="text" name="nick" value="${criterion?.nick}"></input>
    <label for="search-message"><g:message code="mixedViewer.search.message" />:</label>
      <input id="search-message" type="text" name="message" value="${criterion?.message}"></input>
    <my:ifTypeVisible>   
    <label for="search-type"><g:message code="mixedViewer.search.type" />:</label>
      <g:select id="search-type" name="type" from="${Irclog.TYPE_LIST}" value="${criterion?.type}" valueMessagePrefix="mixedViewer.search.type" />
    </my:ifTypeVisible>   
    <input id="search-submit" class="image" type="image" src="${createLinkTo(dir:'images',file:'search.png')}" title="${message(code:'mixedViewer.search.button.tooltips')}" />
      <label for="search-submit" id="search-submit-label"><g:message code="mixedViewer.search.button" /></label>
  </g:form>
</div>