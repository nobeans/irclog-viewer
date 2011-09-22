<div class="search">
  <g:form method="get" name="search">
    <label for="search-period"><g:message code="mixedViewer.search.period" />:</label>
      <g:select id="search-period" name="period" from="${selectablePeriods}" value="${criterion?.period}" valueMessagePrefix="mixedViewer.search.period" />
      <span id="period-oneday-select" ${(criterion?.period == 'oneday') ? '' : 'style="display:none"'}>
        <input type="text" class="datepicker" name="period-oneday-date" value="${criterion['period-oneday-date']}" />
      </span>
    <label class="searchItem" for="search-channel"><g:message code="mixedViewer.search.channel" />:</label>
      <g:select id="search-channel" name="channel" from="${selectableChannels}" value="${criterion?.channel}" optionKey="key" optionValue="value" />
    <label class="searchItem" for="search-nick"><g:message code="mixedViewer.search.nick" />:</label>
      <input id="search-nick" type="text" name="nick" value="${criterion?.nick}"></input>
    <label class="searchItem" for="search-message"><g:message code="mixedViewer.search.message" />:</label>
      <input id="search-message" type="text" name="message" value="${criterion?.message}"></input>
    <span class="searchItem" title="${message(code:'mixedViewer.search.type.tooltips')}"><g:checkBox id="search-type" name="type" value="all" checked="${criterion?.type == 'all'}" /><label for="search-type" id="search-type-label"><g:message code="mixedViewer.search.type.all" /></label></span>
    <span class="searchItem" title="${message(code:'mixedViewer.search.clear.button.tooltips')}"><input id="search-clear" class="image" type="image" src="${resource(dir:'images',file:'searchClear.png')}" name="_action_clearCriterion" alt="Clear" /><label for="search-clear" id="search-clear-label"><g:message code="${message(code:'mixedViewer.search.clear.button')}" /></label></span>
    <span class="searchItem" title="${message(code:'mixedViewer.search.button.tooltips')}"><input id="search-submit" class="image" type="image" src="${resource(dir:'images',file:'search.png')}" alt="Search" /><label for="search-submit" id="search-submit-label"><g:message code="mixedViewer.search.button" /></label></span>
  </g:form>
</div>
