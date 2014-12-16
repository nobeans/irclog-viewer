<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <title><g:layoutTitle default="${message(code: 'application.name')}"/></title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="shortcut icon" href="${asset.assetPath(src: 'favicon.png')}" type="image/x-icon"/>
  <link rel="apple-touch-icon" href="${asset.assetPath(src: 'apple-touch-icon.png')}">
  <link rel="apple-touch-icon" sizes="114x114" href="${asset.assetPath(src: 'apple-touch-icon-retina.png')}">
  <asset:stylesheet src="application.css"/>
  <g:layoutHead/>
</head>

<g:set var="locale" value="${org.springframework.web.servlet.support.RequestContextUtils.getLocale(request)}"/>
<g:set var="serverName" value="${request.serverName}"/>
<body data-locale="${locale}" data-lang="${locale.language}" data-country="${locale.country}" data-server-name="${serverName}" data-controller="${controllerName}" data-action="${actionName}">
<div class="header">
  <h1><img class="title" src="${asset.assetPath(src: 'headerTitle.png')}" alt="${message(code: "application.name")}"/></h1>
</div>

<div class="nav">
  <g:render template="../layouts/nav"/>
</div>

<!--[if lte IE 8.0]>
<div id="legacy-browser"><g:message code="header.legacy-browser.message"/></div>
<![endif]-->

<!--[if (gt IE 9)|!(IE)]><!-->

<g:layoutBody/>

<div class="footer" role="contentinfo">
  <div class="right">
    <div id="poweredBy"><g:message code="footer.poweredBy" args="${[grails.util.Holders.grailsApplication.metadata.getGrailsVersion()]}"/></div>

    <div id="responseTime">
      <g:if test="${request.startTime}">
        <% def responseTime = String.format("%.3f", (System.currentTimeMillis() - request.startTime) / 1000.0) %>
        <g:message code="footer.responseTime" args="${[responseTime]}"/>
      </g:if>
    </div>
  </div>
</div>

<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>

<asset:javascript src="application.js"/>

<!--<![endif]-->
</body>
</html>
