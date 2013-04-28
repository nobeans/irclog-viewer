<div class="search">
  <g:form method="get" name="search">
    <label for="search-period"><g:message code="search.criteria.period"/>:</label>
    <g:select id="search-period" name="period" from="${query.periodCandidates}" value="${query.period}" valueMessagePrefix="search.criteria.period"/>
    <span id="period-oneday-select" ${(query.period == 'oneday') ? '' : 'style="display:none"'}>
      <input type="text" class="datepicker" name="periodOnedayDate" value="${query.periodOnedayDate?.encodeAsHTML()}"/>
    </span>

    <label class="searchItem" for="search-channel"><g:message code="search.criteria.channel"/>:</label>
    <g:select id="search-channel" name="channel" from="${query.channelCandidates}" value="${query.channel}" valueMessagePrefix="search.criteria.channel"/>

    <label class="searchItem" for="search-nick"><g:message code="search.criteria.nick"/>:</label>
    <input id="search-nick" type="text" name="nick" value="${query.nick?.encodeAsHTML()}"/>

    <label class="searchItem" for="search-message"><g:message code="search.criteria.message"/>:</label>
    <input id="search-message" type="text" name="message" value="${query.message?.encodeAsHTML()}"/>

    <span class="searchItem" title="${message(code: 'search.criteria.type.tooltips')}"><g:checkBox id="search-type" name="type" value="all" checked="${query.type == 'all'}"/><label for="search-type" id="search-type-label"><g:message code="search.criteria.type.all"/></label></span>
    <span class="searchItem" title="${message(code: 'search.criteria.clear.button.tooltips')}"><input id="search-clear" class="image" type="image" src="${resource(dir: 'images', file: 'searchClear.png')}" name="_action_clearCriteria" alt="Clear"/><label for="search-clear" id="search-clear-label"><g:message code="${message(code: 'search.criteria.clear.button')}"/></label></span>
    <span class="searchItem" title="${message(code: 'search.criteria.button.tooltips')}"><input id="search-submit" class="image" type="image" src="${resource(dir: 'images', file: 'search.png')}" alt="Search"/><label for="search-submit" id="search-submit-label"><g:message code="search.criteria.button"/></label></span>
  </g:form>
</div>
