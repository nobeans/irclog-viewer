<div class="search">
  <g:form method="get" name="search">
    <label for="search-period"><g:message code="mixedViewer.search.period"/>:</label>
    <g:select id="search-period" name="period" from="${query.periodCandidates}" value="${query.period}" valueMessagePrefix="mixedViewer.search.period"/>
    <span id="period-oneday-select" ${(query.period == 'oneday') ? '' : 'style="display:none"'}>
      <input type="text" class="datepicker" name="periodOnedayDate" value="${query.periodOnedayDate?.encodeAsHTML()}"/>
    </span>

    <label class="searchItem" for="search-channel"><g:message code="mixedViewer.search.channel"/>:</label>
    <g:select id="search-channel" name="channel" from="${query.channelCandidates}" value="${query.channel}" valueMessagePrefix="mixedViewer.search.channel"/>

    <label class="searchItem" for="search-nick"><g:message code="mixedViewer.search.nick"/>:</label>
    <input id="search-nick" type="text" name="nick" value="${query.nick?.encodeAsHTML()}"/>

    <label class="searchItem" for="search-message"><g:message code="mixedViewer.search.message"/>:</label>
    <input id="search-message" type="text" name="message" value="${query.message?.encodeAsHTML()}"/>

    <span class="searchItem" title="${message(code: 'mixedViewer.search.type.tooltips')}"><g:checkBox id="search-type" name="type" value="all" checked="${query.type == 'all'}"/><label for="search-type" id="search-type-label"><g:message code="mixedViewer.search.type.all"/></label></span>
    <span class="searchItem" title="${message(code: 'mixedViewer.search.clear.button.tooltips')}"><input id="search-clear" class="image" type="image" src="${resource(dir: 'images', file: 'searchClear.png')}" name="_action_clearCriteria" alt="Clear"/><label for="search-clear" id="search-clear-label"><g:message code="${message(code: 'mixedViewer.search.clear.button')}"/></label></span>
    <span class="searchItem" title="${message(code: 'mixedViewer.search.button.tooltips')}"><input id="search-submit" class="image" type="image" src="${resource(dir: 'images', file: 'search.png')}" alt="Search"/><label for="search-submit" id="search-submit-label"><g:message code="mixedViewer.search.button"/></label></span>
  </g:form>
</div>
