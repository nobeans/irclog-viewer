<div class="list">
  <div class="paginateButtons top">
    <g:paginate total="${irclogCount}" params="${criterion}" />
    <span class="count">${Math.min(params.offset + 1, irclogCount)} - ${Math.min(params.offset + params.max, irclogCount)} 件表示 / ${irclogCount} 件中</span>
  </div>
  <table>
    <thead>
      <tr>
        <th class="irclog-single"  title="${message(code:'mixedViewer.list.single.tooltips')}">
          <img src="${createLinkTo(dir:'images',file:'singleTitle.png')}" />
        </th>
        <th class="irclog-time" title="${message(code:'mixedViewer.list.time.tooltips')}"><g:message code="irclog.time"/></th>
        <th class="irclog-channel" title="${message(code:'mixedViewer.list.channel.tooltips')}"><g:message code="irclog.channel"/></th>
        <th class="irclog-nick"><g:message code="irclog.nick"/></th>
        <th class="irclog-message"><g:message code="irclog.message"/></th>
        <my:ifTypeVisible><th class="irclog-type"><nobr><g:message code="irclog.type"/></nobr></th></my:ifTypeVisible>
      </tr>
    </thead>
    <tbody>
    <g:each in="${irclogList}" status="i" var="irclog">
      <tr class="${(i % 2) == 0 ? 'odd' : 'even'} ${irclog.type}">
        <td class="irclog-single"><my:singleLink permaId="${irclog.permaId}" time="${irclog.time}" channelName="${irclog.channel.name}" image="single.gif"/></td>
        <td class="irclog-time"><my:onedayLink time="${irclog.time}" params="${criterion}" /></td>
        <td class="irclog-channel"><my:channelLink channel="${irclog.channel}" params="${criterion}" /></td>
        <td class="irclog-nick ${irclog.nick?.encodeAsHTML()}">${irclog.nick?.encodeAsHTML()}</td>
        <td class="irclog-message"><my:messageFormat value="${irclog.message}" /></td>
        <my:ifTypeVisible><td class="irclog-type">${irclog.type}</td></my:ifTypeVisible>
      </tr>
    </g:each>
    </tbody>
  </table>
  <div class="paginateButtons bottom">
    <g:paginate total="${irclogCount}" params="${criterion}" />
    <span class="count">${Math.min(params.offset + 1, irclogCount)} - ${Math.min(params.offset + params.max, irclogCount)} 件表示 / ${irclogCount} 件中</span>
  </div>
</div>
