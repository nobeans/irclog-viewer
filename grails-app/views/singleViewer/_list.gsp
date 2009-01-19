<div class="list">
  <table>
    <caption>
      <my:selectChannelForSingle from="${selectableChannels}" value="${criterion?.channel}" date="${criterion['period-oneday-date']}" />
      at
      <my:singleLink time="${beforeDate}" channelName="${criterion.channel}" image="singleBefore.png" />
      ${criterion['period-oneday-date']}
      <my:singleLink time="${afterDate}" channelName="${criterion.channel}" image="singleAfter.png" />
    </caption>
    <thead>
      <tr>
        <th class="irclog-time"><g:message code="irclog.time"/></th>
        <th class="irclog-nick"><g:message code="irclog.nick"/></th>
        <th class="irclog-message">
          <span><g:message code="irclog.message"/></span>
          <span class="optionButtons">
              <button id="toggleTypeFilter-all" onclick="IRCLOG.showAllType()" style="display:none"
                title="${message(code:'singleViewer.toggleTypeFilter.button.tooltips.all')}">
                <g:message code="singleViewer.toggleTypeFilter.button.all" />
              </button>
              <button id="toggleTypeFilter-filtered" onclick="IRCLOG.hideControlType()"
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
    <% def mandatoryTypeList = ['PRIVMSG', 'NOTICE'] %>
    <g:each in="${irclogList}" status="i" var="irclog">
      <tr id="${irclog.permaId}"
          class="${(i % 2) == 0 ? 'odd' : 'even'}
                 ${irclog.type}
                 ${mandatoryTypeList.contains(irclog.type) ? 'mandatoryType' : 'optionType'}
                 clickable"
                 onclick="IRCLOG.highlightLine('${irclog.permaId}');document.location='#${irclog.permaId}'">
        <td class="irclog-time"><my:dateFormat value="${irclog.time}" format="HH:mm:ss" /></td>
        <td class="irclog-nick">${irclog.nick?.encodeAsHTML()}</td>
        <td class="irclog-message"><my:messageFormat value="${irclog.message}" /></td>
      </tr>
    </g:each>
    </tbody>
  </table>
</div>
