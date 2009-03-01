<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <title><g:message code="channel.create" default="Create Channel" /></title>
  </head>
  <body>
    <div class="body channel">
      <my:flashMessage bean="${channel}" />
      <my:withHelp id="channel-create-caption">
        <h1><g:message code="channel.create" default="Create Channel" /></h1>
      </my:withHelp>
      <my:help for="channel-create-caption" visible="true">
        <g:message code="channel.create.caption" />
      </my:help>
      <g:form action="save" method="post" >
        <div class="dialog">
          <table>
            <tbody>
              <tr class="prop">
                <th valign="top" class="name">
                  <label for="name"><g:message code="channel.name" default="Name" />:</label>
                </th>
                <td valign="top" class="value ${hasErrors(bean:channel,field:'name','errors')}">
                  <input type="text" id="name" name="name" value="${fieldValue(bean:channel,field:'name')}"/>
                </td>
              </tr> 
              <tr class="prop">
                <th valign="top" class="name">
                  <label for="description"><g:message code="channel.description" default="Description" />:</label>
                </th>
                <td valign="top" class="value ${hasErrors(bean:channel,field:'description','errors')}">
                  <textarea id="description" name="description">${fieldValue(bean:channel,field:'description')}</textarea>
                </td>
              </tr> 
              <tr class="prop">
                <th valign="top" class="name">
                  <label for="isPrivate"><g:message code="channel.isPrivate" default="Is Private" />:</label>
                </th>
                <td valign="top" class="value ${hasErrors(bean:channel,field:'isPrivate','errors')}">
                  <g:checkBox name="isPrivate" value="${channel?.isPrivate}" ></g:checkBox><br/>
                  <g:message code="channel.isPrivate.editCaption" />
                </td>
              </tr> 
              <tr class="prop">
                <th valign="top" class="name">
                  <label for="secretKey"><g:message code="channel.secretKey" default="Secret Key" />:</label>
                </th>
                <td valign="top" class="value ${hasErrors(bean:channel,field:'secretKey','errors')}">
                  <input type="password" id="secretKey" name="secretKey" value="${fieldValue(bean:channel,field:'secretKey')}"/>
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
