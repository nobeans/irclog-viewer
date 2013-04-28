<div class="list">
  <div class="paginateButtons top">
    <g:paginate total="${irclogTotalCount}" max="${query.max}" offset="${query.offset}" params="${criteriaMap}"/>
    <% int beginIndex = Math.min(query.offset + 1, irclogTotalCount) %>
    <% int endIndex = Math.min(query.offset + query.max, irclogTotalCount) %>
    <% int totalCount = irclogTotalCount %>
    <span class="count"><g:message code="paginate.count" args="${[beginIndex, endIndex, totalCount]}"/></span>
  </div>

  <table>
    <thead>
    <tr>
      <th class="irclog-detail" title="${message(code: 'search.list.detail.tooltips')}">
        <img src="${resource(dir: 'images', file: 'detailTitle.png')}" alt="Link to detail viewer"/>
      </th>
      <th class="irclog-time" title="${message(code: 'search.list.time.tooltips')}"><g:message code="irclog.time"/></th>
      <th class="irclog-channel" title="${message(code: 'search.list.channel.tooltips')}"><g:message code="irclog.channel"/></th>
      <th class="irclog-nick"><g:message code="irclog.nick"/></th>
      <th class="irclog-message"><g:message code="irclog.message"/></th>
    </tr>
    </thead>
    <tbody>
    <% def isEssentialType = { type -> essentialTypes.contains(type) } %>
    <g:each in="${irclogList}" status="i" var="irclog">
      <tr class="${(i % 2) == 0 ? 'odd' : 'even'} ${irclog.type} ${isEssentialType(irclog.type) ? 'essentialType' : 'optionType'}">
        <td class="irclog-detail"><irclog:detailLink permaId="${irclog.permaId}" time="${irclog.time}" channelName="${irclog.channel.name}" image="detail.gif"/></td>
        <td class="irclog-time"><irclog:timeLink time="${irclog.time}" params="${criteriaMap}"/></td>
        <td class="irclog-channel" title="${irclog.channel.description}"><irclog:channelLink channel="${irclog.channel}" params="${criteriaMap}"/></td>
        <td class="irclog-nick ${irclog.nick?.encodeAsHTML()}">${irclog.nick?.encodeAsHTML()}</td>
        <td class="irclog-message wordBreak"><irclog:messageFormat value="${irclog.message}"/></td>
      </tr>
    </g:each>
    </tbody>
  </table>

  <div class="paginateButtons bottom">
    <g:paginate total="${irclogTotalCount}" max="${query.max}" offset="${query.offset}" params="${criteriaMap}"/>
    <span class="count"><g:message code="paginate.count" args="${[beginIndex, endIndex, totalCount]}"/></span>
  </div>
</div>
