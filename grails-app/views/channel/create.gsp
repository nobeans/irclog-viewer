

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title><g:message code="channel.create" default="Create Channel" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}"><g:message code="home" default="Home" /></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="channel.list" default="Channel List" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="channel.create" default="Create Channel" /></h1>
            <g:if test="${flash.message}">
            <div class="message"><g:message code="${flash.message}" args="${flash.args}" default="${flash.defaultMessage}" /></div>
            </g:if>
            <g:hasErrors bean="${channel}">
            <div class="errors">
                <g:renderErrors bean="${channel}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name"><g:message code="channel.name" default="Name" />:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:channel,field:'name','errors')}">
                                    <input type="text" id="name" name="name" value="${fieldValue(bean:channel,field:'name')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="description"><g:message code="channel.description" default="Description" />:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:channel,field:'description','errors')}">
                                    <input type="text" id="description" name="description" value="${fieldValue(bean:channel,field:'description')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="isPrivate"><g:message code="channel.isPrivate" default="Is Private" />:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:channel,field:'isPrivate','errors')}">
                                    <g:checkBox name="isPrivate" value="${channel?.isPrivate}" ></g:checkBox>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="secretKey"><g:message code="channel.secretKey" default="Secret Key" />:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:channel,field:'secretKey','errors')}">
                                    <input type="text" id="secretKey" name="secretKey" value="${fieldValue(bean:channel,field:'secretKey')}"/>
                                </td>
                            </tr> 
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><input class="save" type="submit" value="${message(code:'create', 'default':'Create')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
