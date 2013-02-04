<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title><g:layoutTitle default="${message(code:'application.name')}" /></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.png')}" type="image/x-icon" />
    <link rel="apple-touch-icon" href="${resource(dir: 'images', file: 'apple-touch-icon.png')}">
    <link rel="apple-touch-icon" sizes="114x114" href="${resource(dir: 'images', file: 'apple-touch-icon-retina.png')}">
    <r:require module="application" />
    <g:layoutHead />
    <r:layoutResources/>
  </head>
  <body>
    <div class="header">
      <h1><img class="title" src="${resource(dir:'images', file:'headerTitle.png')}" alt="${message(code:"application.name")}" /></h1>
      <div id="analog"><g:message code="header.analog" /></div>
    </div>
    <div class="nav">
      <g:render template="../layouts/nav" />
    </div>
    <g:layoutBody />
    <div class="footer" role="contentinfo">
      <div class="right">
        <div id="poweredBy"><g:message code="footer.poweredBy" /></div>
        <div id="responseTime">
          <g:if test="${request.startTime}">
            <% def responseTime = String.format("%.3f", (System.currentTimeMillis() - request.startTime) / 1000.0 ) %>
            <g:message code="footer.responseTime" args="${[responseTime]}" />
          </g:if>
        </div>
      </div>
    </div>
    <div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
    <r:layoutResources/>
  </body>
</html>
