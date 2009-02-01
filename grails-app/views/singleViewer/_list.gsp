<div class="caption">
  <my:selectChannelForSingle from="${selectableChannels}" value="${criterion?.channel}" date="${criterion['period-oneday-date']}" />
  at
  <my:singleLink time="${beforeDate}" channelName="${criterion.channel}" image="singleBefore.png" />
  ${criterion['period-oneday-date']}
  <my:singleLink time="${afterDate}" channelName="${criterion.channel}" image="singleAfter.png" />
</div>
<div class="list">
  <table>
    <thead>
      <tr>
        <th class="irclog-time"><g:message code="irclog.time"/></th>
        <th class="irclog-nick"><g:message code="irclog.nick"/></th>
        <th class="irclog-message">
          <span><g:message code="irclog.message"/></span>
          <span class="optionButtons">
            <% def isCurrentTypeEqualsAll = (criterion?.currentType == 'all') %>
            <button id="toggleTypeFilter-all" onclick="IRCLOG.showAllType()"
              style="${isCurrentTypeEqualsAll ? 'display:none' : ''}"
              ${irclogList.empty ? 'disabled="disabled"' : ''}
              title="${message(code:'singleViewer.toggleTypeFilter.button.tooltips.all')}">
              <g:message code="singleViewer.toggleTypeFilter.button.all" />
            </button>
            <button id="toggleTypeFilter-filtered" onclick="IRCLOG.hideControlType()"
              style="${isCurrentTypeEqualsAll ? '' : 'display:none'}"
              ${irclogList.empty ? 'disabled="disabled"' : ''}
              title="${message(code:'singleViewer.toggleTypeFilter.button.tooltips.filtered')}">
              <g:message code="singleViewer.toggleTypeFilter.button.filtered" />
            </button>
            <button id="clearHighlight" onclick="IRCLOG.highlightLine('');document.location='#'"
              title="${message(code:'singleViewer.clearHighlight.button.tooltips')}">
              <g:message code="singleViewer.clearHighlight.button" />
            </button>
          </span>
        </th>
      </tr>
    </thead>
    <tbody>
      <% def isMandatoryType = { type -> ['PRIVMSG', 'NOTICE', 'TOPIC'].contains(type) } %>
      <% def isDefaultHiddenType = { type -> !isCurrentTypeEqualsAll && !isMandatoryType(type) } %>
      <g:each in="${irclogList}" status="i" var="irclog">
        <tr id="${irclog.permaId}"
            class="${(i % 2) == 0 ? 'odd' : 'even'} ${irclog.type} ${isMandatoryType(irclog.type) ? 'mandatoryType' : 'optionType'} clickable"
            style="${isDefaultHiddenType(irclog.type) ? 'display:none' : ''}"
            onclick="IRCLOG.highlightLine('${irclog.permaId}');document.location='#${irclog.permaId}'">
          <td class="irclog-time"><my:dateFormat value="${irclog.time}" format="HH:mm:ss" /></td>
          <td class="irclog-nick ${irclog.nick?.encodeAsHTML()}">${irclog.nick?.encodeAsHTML()}</td>
          <td class="irclog-message wordBreak"><my:messageFormat value="${irclog.message}" /></td>
        </tr>
      </g:each>
    </tbody>
  </table>
</div>
