package irclog

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import irclog.controller.Base

class LogoutController extends Base {
    
    def index = { 
        redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl // '/j_spring_security_logout'
    }
}
