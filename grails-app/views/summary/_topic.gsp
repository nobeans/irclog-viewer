<div class="summary-topic">
  <my:withHelp id="summary-topic-help">
    <h1><g:message code="summary.topic" /></h1>
  </my:withHelp>
  <my:help for="summary-topic-help">
    <g:message code="summary.topic.caption" />
  </my:help>
  <ul class="list">
    <g:each in="${topicList}" var="irclog">
      <li>
        <my:singleLink permaId="${irclog.permaId}" time="${irclog.time}" channelName="${irclog.channel.name}">
          [${new java.text.SimpleDateFormat('yyyy-MM-dd HH:mm:ss').format(irclog.time)}] ${irclog.channelName} ${irclog.message}
        </my:singleLink>
      </li>
    </g:each>
    <li class="summary-topic-search"><my:topicLink><g:message code="summary.topic.search" /></my:topicLink></li>
  </ul>
</div>
