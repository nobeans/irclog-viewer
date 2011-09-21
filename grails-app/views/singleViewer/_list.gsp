<div class="caption">
  <my:selectChannelForSingle from="${selectableChannels}" value="${criterion.channel}" date="${criterion['period-oneday-date']}" />
  at
  <my:singleLink time="${relatedDates.before}" channelName="${criterion.channel}" image="singleBefore.png" />
  <span id="currentDate">${criterion['period-oneday-date']}</span>
  <my:singleLink time="${relatedDates.after}" channelName="${criterion.channel}" image="singleAfter.png" />
  <my:singleLink time="${relatedDates.latest}" channelName="${criterion.channel}" image="singleToday.png" />
  <input type="text" class="datepicker" style="display:none" value="${criterion['period-oneday-date']}" />
</div>
<div class="list">
  <div class="paginateButtons top">
    <% def essentialTypeCount = irclogList.findAll{essentialTypes.contains(it.type)}.size() %>
    <span class="count"><g:message code="singleViewer.count" args="${[irclogList.size(), essentialTypeCount]}"/></span>
  </div>
  <table>
    <thead>
      <tr>
        <th class="irclog-time"><g:message code="irclog.time"/></th>
        <th class="irclog-nick"><g:message code="irclog.nick"/></th>
        <th class="irclog-message">
          <span><g:message code="irclog.message"/></span>
          <span class="optionButtons">
            <button id="toggleTypeFilter-all"
              ${criterion.isIgnoredOptionType ? '' : 'style="display:none"'}
              ${irclogList.empty ? 'disabled="disabled"' : ''}
              title="${message(code:'singleViewer.toggleTypeFilter.button.tooltips.all')}">
              <g:message code="singleViewer.toggleTypeFilter.button.all" />
            </button>
            <button id="toggleTypeFilter-filtered"
              ${criterion.isIgnoredOptionType ? 'style="display:none"': ''}
              ${irclogList.empty ? 'disabled="disabled"' : ''}
              title="${message(code:'singleViewer.toggleTypeFilter.button.tooltips.filtered')}">
              <g:message code="singleViewer.toggleTypeFilter.button.filtered" />
            </button>
            <button id="clearHighlight" disabled="disabled" title="${message(code:'singleViewer.clearHighlight.button.tooltips')}">
              <g:message code="singleViewer.clearHighlight.button" />
            </button>
          </span>
        </th>
      </tr>
    </thead>
    <% def isEssentialType = { type -> essentialTypes.contains(type) } %>
    <% def isDefaultHiddenType = { type -> criterion.isIgnoredOptionType && !isEssentialType(type) } %>
    <tbody>
      <g:each in="${irclogList}" status="i" var="irclog">
        <tr id="pid-${irclog.permaId}"
            class="irclog ${(i % 2) == 0 ? 'odd' : 'even'} ${irclog.type} ${isEssentialType(irclog.type) ? 'essentialType' : 'optionType'}"
            ${isDefaultHiddenType(irclog.type) ? 'style="display:none"' : ''} >
          <td class="irclog-time"><my:dateFormat value="${irclog.time}" format="HH:mm:ss" /></td>
          <td class="irclog-nick ${irclog.nick?.encodeAsHTML()}" title="${getPersonByNick(irclog.nick)?.realName?.encodeAsHTML() ?: ''}">${irclog.nick?.encodeAsHTML()}</td>
          <td class="irclog-message wordBreak"><my:messageFormat value="${irclog.message}" /></td>
        </tr>
      </g:each>
    </tbody>
  </table>
</div>
