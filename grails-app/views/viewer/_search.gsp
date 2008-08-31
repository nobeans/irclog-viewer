<div id="search">
  <h1><g:message code="search.title" /></h1>
  <g:form action="index" method="post" >
    <label for="search-channel"><g:message code="search.channel" />:</label>
      <g:select name="channelId" from="${selectableChannels}" value="${criterion?.channelId}" optionKey="key" optionValue="value" />
    <label for="search-scope"><g:message code="search.scope" />:</label>
      <g:select name="scope" from="${selectableScopes}" value="${criterion?.scope}" optionKey="key" optionValue="value" />
    <label for="search-nick"><g:message code="search.nick" />:</label>
      <input id="search-nick" type="text" name="nick" value="${criterion?.nick}"></input>
    <label for="search-message"><g:message code="search.message" />:</label>
      <input id="search-message" type="text" name="message" value="${criterion?.message}"></input>
    <span class="button"><g:actionSubmit class="search" action="index" value="${message(code:'search')}" /></span>
  </g:form>
</div>
