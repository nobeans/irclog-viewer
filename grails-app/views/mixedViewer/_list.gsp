<div class="list">
  <div class="paginateButtons top">
    <g:paginate total="${irclogCount}" params="${criterion}" />
    <% def beginIndex = Math.min(params.offset + 1, irclogCount) %>
    <% def endIndex = Math.min(params.offset + params.max, irclogCount) %>
    <% def totalCount = irclogCount %>
    <span class="count"><g:message code="paginate.count" args="${[beginIndex, endIndex, totalCount]}"/></span>
  </div>
  <table>
    <thead>
      <tr>
        <th class="irclog-single"  title="${message(code:'mixedViewer.list.single.tooltips')}">
          <img src="${resource(dir:'images',file:'singleTitle.png')}" alt="Link to single viewer" />
        </th>
        <th class="irclog-time" title="${message(code:'mixedViewer.list.time.tooltips')}"><g:message code="irclog.time"/></th>
        <th class="irclog-channel" title="${message(code:'mixedViewer.list.channel.tooltips')}"><g:message code="irclog.channel"/></th>
        <th class="irclog-nick"><g:message code="irclog.nick"/></th>
        <th class="irclog-message"><g:message code="irclog.message"/></th>
      </tr>
    </thead>
    <tbody>
      <% def isEssentialType = { type -> essentialTypes.contains(type) } %>
      <g:each in="${irclogList}" status="i" var="irclog">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'} ${irclog.type} ${isEssentialType(irclog.type) ? 'essentialType' : 'optionType'}">
          <td class="irclog-single"><irclog:singleLink permaId="${irclog.permaId}" time="${irclog.time}" channelName="${irclog.channel.name}" image="single.gif"/></td>
          <td class="irclog-time"><irclog:timeLink time="${irclog.time}" params="${criterion}" /></td>
          <td class="irclog-channel" title="${irclog.channel.description}"><irclog:channelLink channel="${irclog.channel}" params="${criterion}" /></td>
          <td class="irclog-nick ${irclog.nick?.encodeAsHTML()}" title="${getPersonByNick(irclog.nick)?.realName?.encodeAsHTML() ?: ''}">${irclog.nick?.encodeAsHTML()}</td>
          <td class="irclog-message wordBreak"><irclog:messageFormat value="${irclog.message}" /></td>
        </tr>
      </g:each>
    </tbody>
  </table>
  <div class="paginateButtons bottom">
    <g:paginate total="${irclogCount}" params="${criterion}" />
    <span class="count"><g:message code="paginate.count" args="${[beginIndex, endIndex, totalCount]}"/></span>
  </div>
</div>
