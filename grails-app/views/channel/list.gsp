

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title><g:message code="channel.list" default="Channel List" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}"><g:message code="home" default="Home" /></a></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="channel.new" default="New Channel" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="channel.list" default="Channel List" /></h1>
            <g:if test="${flash.message}">
            <div class="message"><g:message code="${flash.message}" args="${flash.args}" default="${flash.defaultMessage}" /></div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="id" title="Id" titleKey="channel.id" />
                        
                   	        <g:sortableColumn property="name" title="Name" titleKey="channel.name" />
                        
                   	        <g:sortableColumn property="description" title="Description" titleKey="channel.description" />
                        
                   	        <g:sortableColumn property="isPublic" title="Is Public" titleKey="channel.isPublic" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${channelList}" status="i" var="channel">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${channel.id}">${fieldValue(bean:channel, field:'id')}</g:link></td>
                        
                            <td>${fieldValue(bean:channel, field:'name')}</td>
                        
                            <td>${fieldValue(bean:channel, field:'description')}</td>
                        
                            <td>${fieldValue(bean:channel, field:'isPublic')}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${Channel.count()}" />
            </div>
        </div>
    </body>
</html>
