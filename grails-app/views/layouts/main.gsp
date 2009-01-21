<html>
  <head>
    <title><g:layoutTitle default="${message(code:'application.name')}" /></title>
    <link rel="stylesheet" href="${createLinkTo(dir:'css',file:'main.css')}" />
    <link rel="shortcut icon" href="${createLinkTo(dir:'images',file:'favicon.png')}" type="image/x-icon" />
    <g:javascript library="prototype" />
    <g:layoutHead />
  </head>
  <body>
    <div class="header">
      <h1>${message(code:'application.name')}<img class="badge" src="${createLinkTo(dir:'images', file:'beta.png')}" /></h1>
    </div>
    <div class="nav">
      <g:render template="../layouts/nav" />
    </div>
    <g:layoutBody />  
    <div class="footer">
      <a id="linkeToTop" href="#"><img src="${createLinkTo(dir:'images',file:'top.png')}" title="${message(code:"footer.linkToTop.tooltips")}" /></a>
      <span id="poweredBy"><g:message code="footer.poweredBy" /></span>
    </div>
  </body>	
</html>
