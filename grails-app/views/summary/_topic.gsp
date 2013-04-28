<div class="summary-topic">
  <irclog:withHelp id="summary-topic-help">
    <h1><g:message code="summary.topic"/></h1>
  </irclog:withHelp>
  <irclog:help for="summary-topic-help">
    <g:message code="summary.topic.caption"/>
  </irclog:help>
  <ul class="list">
    <g:each in="${topicList}" var="irclog">
      <li>
        <irclog:detailLink permaId="${irclog.permaId}" time="${irclog.time}" channelName="${irclog.channel.name}">
          [${irclog.time.format('yyyy-MM-dd HH:mm:ss')}] ${irclog.channelName} ${irclog.message}
        </irclog:detailLink>
      </li>
    </g:each>
    <li class="summary-topic-search">
      <%--
       // MEMO:
       // period指定がないとセッションの検索条件よりも優先されないことに注意すること。
       // 個別のtypeによる検索UIを提供していないため、その後ユーザが他の条件と組み合わせて検索しやすいmessage条件を使うようにした。
       // 将来的にtype検索を提供するのであれば、ここでもtype検索にすればよい。
       //def params = [type:"TOPIC", period:"all"]
      --%>
      <g:link controller="search" action="index" params="[message: 'TOPIC:', period: 'all']">
        <g:message code="summary.topic.search"/>
      </g:link>
    </li>
  </ul>
</div>
