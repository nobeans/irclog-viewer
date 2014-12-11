<div class="search">
  <g:form method="get" name="search">
    <label for="search-period"><g:message code="search.criteria.period.label"/>:</label>
    <g:select id="search-period" name="period" from="${periodCandidates}" value="${command.period}" valueMessagePrefix="search.criteria.period.label_of"/>
    <span id="period-oneday-select" ${(command.period == 'oneday') ? '' : 'style="display:none"'}>
      <input type="text" class="datepicker" name="periodOnedayDate" value="${command.periodOnedayDate?.encodeAsHTML()}"/>
    </span>

    <label class="searchItem" for="search-channel"><g:message code="search.criteria.channel.label"/>:</label>
    <g:select id="search-channel" name="channel" from="${channelCandidates}" value="${command.channel}" valueMessagePrefix="search.criteria.channel.label_of"/>

    <label class="searchItem" for="search-nick"><g:message code="search.criteria.nick.label"/>:</label>
    <input id="search-nick" type="text" name="nick" value="${command.nick?.encodeAsHTML()}"/>

    <label class="searchItem" for="search-message"><g:message code="search.criteria.message.label"/>:</label>
    <input id="search-message" type="text" name="message" value="${command.message?.encodeAsHTML()}"/>

    <span class="searchItem" title="${message(code: 'search.criteria.type.caption')}"><g:checkBox id="search-type" name="type" value="all" checked="${command.type == 'all'}"/><label for="search-type" id="search-type-label"><g:message code="search.criteria.type.label_of.all"/></label></span>
    <span class="searchItem" title="${message(code: 'search.criteria.clear.button.caption')}"><input id="search-clear" class="image" type="image" src="${asset.assetPath(src: 'searchClear.png')}" name="_action_clearCriteria" alt="Clear"/><label for="search-clear" id="search-clear-label"><g:message code="search.criteria.clear.button.label"/></label></span>
    <span class="searchItem" title="${message(code: 'search.criteria.button.caption')}"><input id="search-submit" class="image" type="image" src="${asset.assetPath(src: 'search.png')}" alt="Search"/><label for="search-submit" id="search-submit-label"><g:message code="search.criteria.button.label"/></label></span>
  </g:form>
</div>
