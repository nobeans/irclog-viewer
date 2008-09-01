<div class="list">
  <h1><g:message code="list.title" /></h1>
  <div class="paginateButtons">
    <g:paginate total="${irclogCount}" params="${criterion}" />
    <span class="info">${Math.min(params.offset + 1, irclogCount)} - ${Math.min(params.offset + params.max, irclogCount)} 件表示 / ${irclogCount} 件中</span>
  </div>
  <table>
    <thead>
      <tr>
        <%--<th>Click!</th>--%>
        <th title="クリックすると、対象期間がその日付の指定日条件に変更されます。ニックネームとメッセージ条件はクリアされます。"><g:message code="irclog.time"/></th>
        <th><g:message code="irclog.nick"/></th>
        <th><g:message code="irclog.message"/></th>
      </tr>
    </thead>
    <tbody>
    <g:each in="${irclogList}" status="i" var="irclog">
      <tr class="${(i % 2) == 0 ? 'odd' : 'even'} irclog-line irclog-${irclog.type}">
        <%--<td><a href="#${irclog.id}">■</a></td>--%>
        <td class="irclog-time"><my:specifiedDateLink value="${irclog.time}" params="${criterion}" /></td>
        <td class="irclog-nick">${irclog.nick?.encodeAsHTML()}</td>
        <td class="irclog-message"><my:irclog value="${irclog.message}" /></td>
      </tr>
    </g:each>
    </tbody>
  </table>
</div>
