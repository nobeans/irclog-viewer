

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title><g:message code="person.list" default="Person List" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}"><g:message code="home" default="Home" /></a></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="person.new" default="New Person" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="person.list" default="Person List" /></h1>
            <g:if test="${flash.message}">
            <div class="message"><g:message code="${flash.message}" args="${flash.args}" default="${flash.defaultMessage}" /></div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="id" title="Id" titleKey="person.id" />
                        
                   	        <g:sortableColumn property="loginName" title="Login Name" titleKey="person.loginName" />
                        
                   	        <g:sortableColumn property="password" title="Password" titleKey="person.password" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${personList}" status="i" var="person">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${person.id}">${fieldValue(bean:person, field:'id')}</g:link></td>
                        
                            <td>${fieldValue(bean:person, field:'loginName')}</td>
                        
                            <td>${fieldValue(bean:person, field:'password')}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${Person.count()}" />
            </div>
        </div>
    </body>
</html>
