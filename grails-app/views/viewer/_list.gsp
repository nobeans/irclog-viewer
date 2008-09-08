<div class="list">
  <div class="paginateButtons top">
    <g:paginate total="${irclogCount}" params="${criterion}" />
    <span class="info">${Math.min(params.offset + 1, irclogCount)} - ${Math.min(params.offset + params.max, irclogCount)} 件表示 / ${irclogCount} 件中</span>
  </div>
  <table>
    <thead>
      <tr>
        <th class="irclog-specified"  title="${message(code:'viewer.list.specified.tooltips')}">
          <img src="${createLinkTo(dir:'images',file:'specified_head.png')}" />
        </th>
        <th class="irclog-time" title="${message(code:'viewer.list.time.tooltips')}"><g:message code="irclog.time"/></th>
        <th class="irclog-channel" title="${message(code:'viewer.list.channel.tooltips')}"><g:message code="irclog.channel"/></th>
        <th class="irclog-nick"><g:message code="irclog.nick"/></th>
        <th class="irclog-message"><g:message code="irclog.message"/></th>
        <th class="irclog-type"><g:message code="irclog.type"/></th>
      </tr>
    </thead>
    <tbody>
    <g:each in="${irclogList}" status="i" var="irclog">
      <tr class="${(i % 2) == 0 ? 'odd' : 'even'} ${irclog.type}">
        <td class="irclog-specified"><my:specifiedLink time="${irclog.time}" channel="${irclog.channel}" params="${criterion}" /></td>
        <td class="irclog-time"><my:onedayLink time="${irclog.time}" params="${criterion}" /></td>
        <td class="irclog-channel"><my:channelLink channel="${irclog.channel}" params="${criterion}" /></td>
        <td class="irclog-nick">${irclog.nick?.encodeAsHTML()}</td>
        <td class="irclog-message"><my:messageFormat value="${irclog.message}" /></td>
        <td class="irclog-type">${irclog.type}</td>
      </tr>
    </g:each>
    </tbody>
  </table>
  <div class="paginateButtons bottom">
    <g:paginate total="${irclogCount}" params="${criterion}" />
    <span class="info">${Math.min(params.offset + 1, irclogCount)} - ${Math.min(params.offset + params.max, irclogCount)} 件表示 / ${irclogCount} 件中</span>
  </div>
</div>
