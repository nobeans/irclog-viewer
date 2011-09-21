<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <title><g:message code="channel.edit" default="Edit Channel" /></title>
  </head>
  <body>
    <div class="body channel">
      <my:flashMessage bean="${channel}" />
      <my:withHelp id="channel-edit-help">
        <h1><g:message code="channel.edit" default="Edit Channel" /></h1>
      </my:withHelp>
      <my:help for="channel-edit-help" visible="true">
        <g:message code="channel.edit.caption" />
      </my:help>
      <g:form method="post" >
        <input type="hidden" name="id" value="${channel?.id}" />
        <div class="dialog">
          <table>
            <tbody>
              <tr class="prop">
                <th valign="top" class="name">
                  <label for="name"><g:message code="channel.name" default="Name" />:</label>
                </th>
                <td valign="top" class="value">${fieldValue(bean:channel,field:'name')}</td>
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
                  <g:checkBox name="isPrivate" value="${channel?.isPrivate}" ></g:checkBox>
                  <label for="isPrivate"><g:message code="channel.isPrivate.editCaption" /></label>
                </td>
              </tr>
              <tr class="prop">
                <th valign="top" class="name">
                  <label for="isArchived"><g:message code="channel.isArchived" default="Is Archived" />:</label>
                </th>
                <td valign="top" class="value ${hasErrors(bean:channel,field:'isArchived','errors')}">
                  <g:checkBox name="isArchived" value="${channel?.isArchived}" ></g:checkBox>
                  <label for="isArchived"><g:message code="channel.isArchived.editCaption" /></label>
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
          <span class="button"><g:actionSubmit class="save" action="Update" value="${message(code:'update', 'default':'Update')}" /></span>
          <span class="button"><g:actionSubmit class="delete" onclick="return confirm('${message(code:'delete.confirm', 'default':'Are you sure?')}');" action="Delete" value="${message(code:'delete', 'default':'Delete')}" /></span>
        </div>
      </g:form>
    </div>
  </body>
</html>
