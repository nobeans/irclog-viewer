<div class="list">
  <div class="paginateButtons top">
    <div class="count-info">
      <div class="count"><g:message code="singleViewer.count"/></div>
      <label class="showingAllTypes">
        <input type="checkBox" data-bind="checked: showingAllTypes"/>
        <g:message code="singleViewer.toggleTypeFilter.button.all"/>
      </label>
    </div>
  </div>
  <table>
    <thead>
    <tr>
      <th class="irclog-time"><g:message code="irclog.time"/></th>
      <th class="irclog-nick"><g:message code="irclog.nick"/></th>
      <th class="irclog-message">
        <span><g:message code="irclog.message"/></span>
      </th>
    </tr>
    </thead>
    <tbody data-bind="foreach: irclogList">
    <tr data-bind="attr: { class: cssClass, id: permaId }, event: { click: toggleHighlight }">
      <td class="irclog-time" data-bind="text: time"></td>
      <td class="irclog-nick" data-bind="text: nick"></td>
      <td class="irclog-message wordBreak" data-bind="text: message"></td>
    </tr>
    <%--
      <g:each in="${irclogList}" status="i" var="irclog">
        <tr id="pid-${irclog.permaId}"
            class="irclog ${(i % 2) == 0 ? 'odd' : 'even'} ${irclog.type} ${isEssentialType(irclog.type) ? 'essentialType' : 'optionType'}"
            ${isDefaultHiddenType(irclog.type) ? 'style="display:none"' : ''} >
          <td class="irclog-time">${irclog.time.format("HH:mm:ss")}</td>
          <td class="irclog-nick ${irclog.nick?.encodeAsHTML()}" title="${getPersonByNick(irclog.nick)?.realName?.encodeAsHTML() ?: ''}">${irclog.nick?.encodeAsHTML()}</td>
          <td class="irclog-message wordBreak"><irclog:messageFormat value="${irclog.message}" /></td>
        </tr>
      </g:each>
    --%>
    </tbody>
  </table>

  <div style="display:none">
    <g:select name="essentialTypes" from="${essentialTypes}" disabled="true"></g:select>
  </div>
</div>
