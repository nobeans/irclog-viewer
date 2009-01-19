<div class="list">
  <table>
    <caption>
      <my:selectChannelForSingle from="${selectableChannels}" value="${criterion?.channel}" date="${criterion['period-oneday-date']}" />
      at
      <my:singleLink time="${beforeDate}" channelName="${criterion.channel}" image="singleBefore.png" />
      ${criterion['period-oneday-date']}
      <my:singleLink time="${afterDate}" channelName="${criterion.channel}" image="singleAfter.png" />
    </caption>
    <thead class="clickable" onclick="IRCLOG.focusMessage('');document.location='#'">
      <tr title="${message(code:'singleViewer.list.header.tooltips')}">
        <th class="irclog-time"><g:message code="irclog.time"/></th>
        <th class="irclog-nick"><g:message code="irclog.nick"/></th>
        <th class="irclog-message"><g:message code="irclog.message"/></th>
      </tr>
    </thead>
    <tbody>
    <g:each in="${irclogList}" status="i" var="irclog">
      <tr id="${irclog.permaId}" class="${(i % 2) == 0 ? 'odd' : 'even'} ${irclog.type} clickable" onclick="IRCLOG.focusMessage('${irclog.permaId}');document.location='#${irclog.permaId}'">
        <td class="irclog-time"><my:dateFormat value="${irclog.time}" format="HH:mm:ss" /></td>
        <td class="irclog-nick">${irclog.nick?.encodeAsHTML()}</td>
        <td class="irclog-message"><my:messageFormat value="${irclog.message}" /></td>
      </tr>
    </g:each>
    </tbody>
  </table>
</div>
