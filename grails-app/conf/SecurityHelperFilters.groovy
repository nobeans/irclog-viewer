import irclog.Person

class SecurityHelperFilters {

    def springSecurityService

    def filters = {
        all(controller:'*', action:'*') {
           before = {
                if (springSecurityService.isLoggedIn()) {
                    // For convenience in controllers or views 
                    request.isLoggedIn = true
                    def authPrincipal = springSecurityService.principal
                    request.loginUserName = authPrincipal.username
                    request.loginUserDomain = Person.get(authPrincipal.id)
                }
            }
        }
    }
}
