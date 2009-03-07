<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Style-Type" content="text/css"></meta>
    <meta http-equiv="Content-Script-Type" content="text/javascript"></meta>
    <title><g:layoutTitle default="${message(code:'application.name')}" /></title>
    <link rel="stylesheet" href="${createLinkTo(dir:'css',file:'main.css')}" media="all" />
    <link rel="stylesheet" href="${createLinkTo(dir:'css', file:'print.css')}" media="print" />
    <link rel="shortcut icon" href="${createLinkTo(dir:'images',file:'favicon.png')}" type="image/x-icon" />
    <g:javascript library="prototype" />
    <g:javascript library="common" />
    <g:layoutHead />
  </head>
  <body>
    <div class="header">
      <h1><img class="title" src="${createLinkTo(dir:'images', file:'headerTitle.png')}" alt="${message(code:"application.name")}" /></h1>
      <g:if test="${session.timeMarker}">
        <% def timeMarker = session.timeMarker.call() %>
        <% def dateHHmm = my.dateFormat(format:'hhmm', value:timeMarker) %>
        <% def dateHHmmDisp = my.dateFormat(format:'HH:mm', value:timeMarker) %>
        <img id="timeMarkerClock" src="${createLinkTo(dir:'images/clock/', dateHHmm + '.gif')}" alt="${dateHHmmDisp}" title="${message(code:'header.timeMarkerClock', args:[dateHHmmDisp])}" />
      </g:if>
      <div id="analog"><g:message code="header.analog" /></div>
    </div>
    <div class="nav">
      <g:render template="../layouts/nav" />
    </div>
    <g:layoutBody />  
    <div class="footer">
      <a id="linkeToTop" href="#"><img src="${createLinkTo(dir:'images',file:'top.png')}" title="${message(code:"footer.linkToTop.tooltips")}" alt="To top" /></a>
      <span id="poweredBy"><g:message code="footer.poweredBy" /></span>
    </div>
  </body>	
</html>
