<div class="list">
  <table>
    <caption>
      <my:selectChannelForSingle from="${selectableChannels}" value="${criterion?.channel}" date="${criterion['period-oneday-date']}" />
      at
      <my:singleLink time="${beforeDate}" channelName="${criterion.channel}" image="singleBefore.png" />
      ${criterion['period-oneday-date']}
      <my:singleLink time="${afterDate}" channelName="${criterion.channel}" image="singleAfter.png" />
    </caption>
    <thead class="clickable" onclick="focusMessage('');document.location='#'">
      <tr>
        <th class="irclog-time" title="${message(code:'viewer.list.time.tooltips')}"><g:message code="irclog.time"/></th>
        <th class="irclog-nick"><g:message code="irclog.nick"/></th>
        <th class="irclog-message"><g:message code="irclog.message"/></th>
      </tr>
    </thead>
    <tbody>
    <script language="javascript">
      // ★リファクタリングする！スクリプトファイルに抽出！★
      var currentMessageId = null;
      function focusMessage(newMessageId) {
          removeStyleClass(currentMessageId, 'this');
          addStyleClass(newMessageId, 'this');
          currentMessageId = newMessageId;
      }
      function addStyleClass(elementId, className) {
          var ele = $(elementId);
          if (ele) ele.addClassName(className);
      }
      function removeStyleClass(elementId, className) {
          var ele = $(elementId);
          if (ele) ele.removeClassName(className);
      }

      Event.observe(window, 'load', function() {
          focusMessage(location.hash.replace('#',''))
      });
    </script>
    <g:each in="${irclogList}" status="i" var="irclog">
      <tr id="${irclog.permaId}" class="${(i % 2) == 0 ? 'odd' : 'even'} ${irclog.type} clickable" onclick="focusMessage('${irclog.permaId}');document.location='#${irclog.permaId}'">
        <td class="irclog-time"><my:dateFormat value="${irclog.time}" format="HH:mm:ss" /></td>
        <td class="irclog-nick">${irclog.nick?.encodeAsHTML()}</td>
        <td class="irclog-message"><my:messageFormat value="${irclog.message}" /></td>
      </tr>
    </g:each>
    </tbody>
  </table>
</div>
