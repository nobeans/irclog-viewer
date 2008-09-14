

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title><g:message code="channel.show" default="Show Channel" /></title>
    </head>
    <body>
        <div class="body">
            <h1><g:message code="channel.show" default="Show Channel" /></h1>
            <g:if test="${flash.message}">
            <div class="message"><g:message code="${flash.message}" args="${flash.args}" default="${flash.defaultMessage}" /></div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="channel.id" default="Id" />:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:channel, field:'id')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="channel.name" default="Name" />:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:channel, field:'name')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="channel.description" default="Description" />:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:channel, field:'description')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="channel.isPrivate" default="Is Private" />:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:channel, field:'isPrivate')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="channel.secretKey" default="Secret Key" />:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:channel, field:'secretKey')}</td>
                            
                        </tr>
                    
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <input type="hidden" name="id" value="${channel?.id}" />
                    <span class="button"><g:actionSubmit class="edit" action="Edit" value="${message(code:'edit', 'default':'Edit')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('${message(code:'delete.confirm', 'default':'Are you sure?')}');" action="Delete" value="${message(code:'delete', 'default':'Delete')}" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
