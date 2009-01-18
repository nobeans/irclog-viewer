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
      <span class="poweredBy">Powered by Grails</span>
    </div>
  </body>	
</html>
