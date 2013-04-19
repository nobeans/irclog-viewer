<div class="list">
  <div class="paginateButtons top">
    <span class="permaLink" data-bind="if: showPermaLink">
      <g:message code="singleViewer.permaLink"/>
      <input type="text" data-bind="value: permaLink, event: { click: focusPermaLink }" readonly="readonly"/>
    </span>
    <span class="count-info">
      <span class="count"><g:message code="singleViewer.count"/></span>
      <label class="showingAllTypes">
        <input type="checkBox" data-bind="checked: showingAllTypes"/>
        <g:message code="singleViewer.toggleTypeFilter.button.all"/>
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
    <tr data-bind="attr: { class: cssClass, id: permaId }, event: { click: toggleHighlight }">
      <td class="irclog-time" data-bind="text: time"></td>
      <td class="irclog-nick" data-bind="text: nick"></td>
      <td class="irclog-message wordBreak" data-bind="text: message"></td>
    </tr>
    </tbody>
  </table>

  <div style="display:none">
    <g:select name="essentialTypes" from="${essentialTypes}" disabled="true"></g:select>
  </div>
</div>
