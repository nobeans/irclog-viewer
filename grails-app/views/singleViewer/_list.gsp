<div class="caption">
  <my:selectChannelForSingle from="${selectableChannels}" value="${criterion?.channel}" date="${criterion['period-oneday-date']}" />
  at
  <my:singleLink time="${beforeDate}" channelName="${criterion.channel}" image="singleBefore.png" />
  ${criterion['period-oneday-date']}
  <my:singleLink time="${afterDate}" channelName="${criterion.channel}" image="singleAfter.png" />
  <my:singleLink time="${latestDate}" channelName="${criterion.channel}" image="singleToday.png" />
</div>
<div class="list">
  <div class="paginateButtons top">
    <% def totalCount = irclogList.size() %>
    <% def essentialTypeCount = irclogList.findAll{(['PRIVMSG', 'NOTICE', 'TOPIC'] as List).contains(it.type)}.size() %>
    <span class="count"><g:message code="singleViewer.count" args="${[totalCount, essentialTypeCount]}"/></span>
  </div>
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
              ${isCurrentTypeEqualsAll ? 'style="display:none"' : ''}
              ${irclogList.empty ? 'disabled="disabled"' : ''}
              title="${message(code:'singleViewer.toggleTypeFilter.button.tooltips.all')}">
              <g:message code="singleViewer.toggleTypeFilter.button.all" />
            </button>
            <button id="toggleTypeFilter-filtered" onclick="IRCLOG.hideControlType()"
              ${isCurrentTypeEqualsAll ? '' : 'style="display:none"'}
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
      <% def isEssentialType = { type -> ['PRIVMSG', 'NOTICE', 'TOPIC'].contains(type) } %>
      <% def isDefaultHiddenType = { type -> !isCurrentTypeEqualsAll && !isEssentialType(type) } %>
      <g:each in="${irclogList}" status="i" var="irclog">
        <tr id="pid-${irclog.permaId}"
            class="${(i % 2) == 0 ? 'odd' : 'even'} ${irclog.type} ${isEssentialType(irclog.type) ? 'essentialType' : 'optionType'} clickable"
            ${isDefaultHiddenType(irclog.type) ? 'style="display:none"' : ''}
            onclick="IRCLOG.highlightLine('pid-${irclog.permaId}');document.location='#pid-${irclog.permaId}'">
          <td class="irclog-time"><my:dateFormat value="${irclog.time}" format="HH:mm:ss" /></td>
          <td class="irclog-nick ${irclog.nick?.encodeAsHTML()}" title="${getPersonByNick(irclog.nick)?.realName?.encodeAsHTML() ?: ''}">${irclog.nick?.encodeAsHTML()}</td>
          <td class="irclog-message wordBreak"><my:messageFormat value="${irclog.message}" /></td>
        </tr>
      </g:each>
    </tbody>
  </table>
</div>
