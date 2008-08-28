

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title><g:message code="person.edit" default="Edit Person" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}"><g:message code="home" default="Home" /></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="person.list" default="Person List" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="person.new" default="New Person" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="person.edit" default="Edit Person" /></h1>
            <g:if test="${flash.message}">
            <div class="message"><g:message code="${flash.message}" args="${flash.args}" default="${flash.defaultMessage}" /></div>
            </g:if>
            <g:hasErrors bean="${person}">
            <div class="errors">
                <g:renderErrors bean="${person}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <input type="hidden" name="id" value="${person?.id}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="loginName"><g:message code="person.loginName" default="Login Name" />:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:person,field:'loginName','errors')}">
                                    <input type="text" id="loginName" name="loginName" value="${fieldValue(bean:person,field:'loginName')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="password"><g:message code="person.password" default="Password" />:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:person,field:'password','errors')}">
                                    <input type="text" id="password" name="password" value="${fieldValue(bean:person,field:'password')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="channels"><g:message code="person.channels" default="Channels" />:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:person,field:'channels','errors')}">
                                    
<ul>
<g:each var="c" in="${person?.channels?}">
    <li><g:link controller="channel" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></li>
</g:each>
</ul>
<g:link controller="channel" params="['person.id':person?.id]" action="create">Add Channel</g:link>

                                </td>
                            </tr> 
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" action="Update" value="${message(code:'update', 'default':'Update')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('${message(code:'delete.confirm', 'default':'Are you sure?')}');" action="Delete" value="${message(code:'delete', 'default':'Delete')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
