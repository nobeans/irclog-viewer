package irclog

import grails.transaction.Transactional
//import groovy.transform.CompileStatic
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

//@CompileStatic
@Transactional(readOnly = true)
class SpringSecurityService {

    def getCurrentUser() {
        def principal = getPrincipal()
        if (!(principal instanceof UserDetails)) {
            return null
        }
        Person.where { loginName == principal.username }.get()
    }

    def getPrincipal() {
        SecurityContextHolder.context?.authentication?.principal
    }
}
