<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Style-Type" content="text/css"></meta>
    <meta http-equiv="Content-Script-Type" content="text/javascript"></meta>
    <title><g:layoutTitle default="${message(code:'application.name')}" /></title>
    <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" media="all" />
    <link rel="stylesheet" href="${resource(dir:'css', file:'print.css')}" media="print" />
    <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.png')}" type="image/x-icon" />
    <g:javascript library="prototype" />
    <g:javascript library="common" />
    <g:layoutHead />
  </head>
  <body>
    <div class="header">
      <h1><img class="title" src="${resource(dir:'images', file:'headerTitle.png')}" alt="${message(code:"application.name")}" /></h1>
      <g:if test="${session.timeMarker}">
        <% def time = session.timeMarker.time %>
        <% def dateHHmm = my.dateFormat(format:'hhmm', value:time) %>
        <% def dateHHmmDisp = my.dateFormat(format:'HH:mm', value:time) %>
        <img id="timeMarkerClock" src="${resource(dir:'images/clock/', dateHHmm + '.gif')}" alt="${dateHHmmDisp}" title="${message(code:'header.timeMarkerClock', args:[dateHHmmDisp])}" />
      </g:if>
      <div id="analog"><g:message code="header.analog" /></div>
    </div>
    <div class="nav">
      <g:render template="../layouts/nav" />
    </div>
    <g:layoutBody />  
    <div class="footer">
      <a id="linkeToTop" href="#"><img src="${resource(dir:'images',file:'top.png')}" title="${message(code:"footer.linkToTop.tooltips")}" alt="To top" /></a>
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
  </body>	
</html>
