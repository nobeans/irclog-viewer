<div class="list">
  <h1><g:message code="list.title" /></h1>
  <div class="paginateButtons top">
    <g:paginate total="${irclogCount}" params="${criterion}" />
    <span class="info">${Math.min(params.offset + 1, irclogCount)} - ${Math.min(params.offset + params.max, irclogCount)} 件表示 / ${irclogCount} 件中</span>
  </div>
  <table>
    <thead>
      <tr>
        <th title="${message(code:'viewer.specified.tooltips')}"><g:message code="viewer.specified.header"/></th>
        <th title="${message(code:'viewer.time.tooltips')}"><g:message code="irclog.time"/></th>
        <th title="${message(code:'viewer.channel.tooltips')}"><g:message code="irclog.channel"/></th>
        <th><g:message code="irclog.nick"/></th>
        <th><g:message code="irclog.message"/></th>
      </tr>
    </thead>
    <tbody>
    <g:each in="${irclogList}" status="i" var="irclog">
      <tr class="${(i % 2) == 0 ? 'odd' : 'even'} irclog-line irclog-${irclog.type}">
        <td class="irclog-specified"><my:specifiedLink time="${irclog.time}" channel="${irclog.channel}" params="${criterion}" /></td>
        <td class="irclog-time"><my:onedayLink time="${irclog.time}" params="${criterion}" /></td>
        <td class="irclog-channel"><my:channelLink channel="${irclog.channel}" params="${criterion}" /></td>
        <td class="irclog-nick">${irclog.nick?.encodeAsHTML()}</td>
        <td class="irclog-message"><my:irclog value="${irclog.message}" /></td>
      </tr>
    </g:each>
    </tbody>
  </table>
  <div class="paginateButtons bottom">
    <g:paginate total="${irclogCount}" params="${criterion}" />
    <span class="info">${Math.min(params.offset + 1, irclogCount)} - ${Math.min(params.offset + params.max, irclogCount)} 件表示 / ${irclogCount} 件中</span>
  </div>
</div>
