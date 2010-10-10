package irclog.controller

import grails.plugins.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import irclog.utils.CollectionUtils
import irclog.Person

abstract class Base {

    def springSecurityService

    def beforeInterceptor = {
        if (springSecurityService.isLoggedIn()) {
            request.isLoggedIn = true

            def authPrincipal = springSecurityService.principal
            request.loginUserName = authPrincipal.username
            request.loginUserDomain = Person.get(authPrincipal.id)
        }
    }
}
