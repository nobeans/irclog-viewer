<div class="list">
  <h1><g:message code="irclog.list" /></h1>
  <div class="paginateButtons">
    <g:paginate total="${irclogCount}" params="${criterion}" />
    <span class="info">${Math.min(criterion.offset + 1, irclogCount)} - ${Math.min(criterion.offset + criterion.max, irclogCount)} 件表示 / ${irclogCount} 件中</span>
  </div>
  <table>
    <thead>
      <tr>
        <%--<th>Click!</th>--%>
        <th><g:message code="irclog.time"/></th>
        <th><g:message code="irclog.nick"/></th>
        <th><g:message code="irclog.message"/></th>
      </tr>
    </thead>
    <tbody>
    <g:each in="${irclogList}" status="i" var="irclog">
      <tr class="${(i % 2) == 0 ? 'odd' : 'even'} ${irclog.type}">
        <%--<td><a href="#${irclog.id}">■</a></td>--%>
        <td class="irclog-time"><g:dateFormat value="${irclog.time}" format="yyyy-MM-dd hh:mm:ss" /></td>
        <td class="irclog-nick">${fieldValue(bean:irclog, field:'nick')}</td>
        <td class="irclog-message">${fieldValue(bean:irclog, field:'message')}</td>
      </tr>
    </g:each>
    </tbody>
  </table>
</div>
