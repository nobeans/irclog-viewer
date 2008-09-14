<html>
  <head>
    <title><g:layoutTitle default="${message(code:'application.name')}" /></title>
    <link rel="stylesheet" href="${createLinkTo(dir:'css',file:'main.css')}" />
    <link rel="shortcut icon" href="${createLinkTo(dir:'images',file:'favicon.png')}" type="image/x-icon" />
    <g:layoutHead />
    <g:javascript library="application" />				
  </head>
  <body>
    <div class="header">
      <h1><g:layoutTitle default="${message(code:'application.name')}" /></h1>
    </div>
    <g:layoutBody />  
  </body>	
</html>
