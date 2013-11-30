<div class="list">
  <div class="paginateButtons top">
    <span class="permaLink" data-bind="if: showPermaLink">
      <g:message code="detail.permaLink"/>
      <input type="text" data-bind="value: permaLink, event: { click: focusPermaLink }" readonly="readonly"/>
    </span>
    <span class="count-info">
      <span class="count"><g:message code="detail.count"/></span>
      <label class="showingAllTypes">
        <input type="checkBox" data-bind="checked: showingAllTypes"/>
        <g:message code="detail.toggleTypeFilter.button.all"/>
      </label>
    </span>
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
    <tr data-bind="attr: { class: cssClassOfRow, id: permaId }, event: { mouseover: showTools, mouseout: hideTools }">
      <td class="irclog-time" data-bind="text: time"></td>
      <td class="" data-bind="text: nick, attr: { class: cssClassOfNick }" datai-bind="event: { click: toggleHighlight }"></td>
      <td class="irclog-message wordBreak">
        <span class="text" data-bind="html: message"></span>
        <img class="hidden permalink" src="${resource(dir: 'images', file: 'permalink.png')}" alt="permalink" data-bind="attr: { class: cssClassOfTools }, event: { click: toggleHighlight }"/>
      </td>
    </tr>
    </tbody>
  </table>
  <div style="display:none">
    <g:select name="essentialTypes" from="${essentialTypes}" disabled="true"/>
  </div>
  <input type="hidden" id="token" value="${token}"/>
  <div id="scrollToBottom" style="display:none">
    <g:message code="detail.scrollToBottom"/>
  </div>
</div>
