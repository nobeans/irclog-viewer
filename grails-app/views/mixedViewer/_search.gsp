<div class="search">
  <g:form method="get">
    <label for="search-period"><g:message code="mixedViewer.search.period" />:</label>
      <g:select id="search-period" name="period" from="${selectablePeriods}" value="${criterion?.period}" valueMessagePrefix="mixedViewer.search.period" />
      <span id="period-oneday-select" ${(criterion?.period == 'oneday') ? '' : 'style="display:none"'}>
        <my:calendar name="period-oneday-date" value="${criterion?.'period-oneday-date'}" title="${message(code:'mixedViewer.search.period.oneday.tooltips')}"/>
      </span>
    <label for="search-channel"><g:message code="mixedViewer.search.channel" />:</label>
      <g:select id="search-channel" name="channel" from="${selectableChannels}" value="${criterion?.channel}" optionKey="key" optionValue="value" />
    <label for="search-nick"><g:message code="mixedViewer.search.nick" />:</label>
      <input id="search-nick" type="text" name="nick" value="${criterion?.nick}"></input>
    <label for="search-message"><g:message code="mixedViewer.search.message" />:</label>
      <input id="search-message" type="text" name="message" value="${criterion?.message}"></input>
    <span title="${message(code:'mixedViewer.search.type.tooltips')}"><g:checkBox id="search-type" name="type" value="all" checked="${criterion?.type == 'all'}" /><label for="search-type"><g:message code="mixedViewer.search.type.all" /></label></span>
    <span title="${message(code:'mixedViewer.search.button.tooltips')}"><input id="search-submit" class="image" type="image" src="${createLinkTo(dir:'images',file:'search.png')}" alt="Search" /><label for="search-submit" id="search-submit-label"><g:message code="mixedViewer.search.button" /></label></span>
    <span title="${message(code:'mixedViewer.search.clear.button.tooltips')}"><g:actionSubmit id="search-clear" action="clearCriterion" value="${message(code:'mixedViewer.search.clear.button')}" /></span>
  </g:form>
</div>
