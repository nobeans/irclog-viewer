<div class="summary-topic">
  <irclog:withHelp id="summary-topic-help">
    <h1><g:message code="summary.topic.label"/></h1>
  </irclog:withHelp>
  <irclog:help for="summary-topic-help">
    <g:message code="summary.topic.caption"/>
  </irclog:help>
  <ul class="list" data-bind="foreach: topicList">
    <li><a href="#" data-bind="attr: { href: topicLink }">[<span data-bind="text: formattedTime"></span>] <span data-bind="text: topic.channelName"></span> -- <span data-bind="text: topic.message"></span> (by <span data-bind="text: topic.nick"></span>)</a></li>
  </ul>
  <ul>
    <li class="summary-topic-search">
      <g:link controller="search" action="index" params="[type: 'TOPIC', period: 'all']">
        <g:message code="summary.topic.search.button.label"/>
      </g:link>
    </li>
  </ul>
  <input type="hidden" id="topic-token" value="${topicToken}"/>
</div>
