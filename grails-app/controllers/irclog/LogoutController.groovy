package irclog

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class LogoutController {
    def index() {
        redirect(uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl)
    }
}
