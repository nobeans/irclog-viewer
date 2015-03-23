package grails.plugin.springsecurity

import grails.transaction.Transactional

@Transactional
class SpringSecurityService {

    def getCurrentUser() {
        Person.list()[0]
    }

    def encodePassword(password) {
        "ENCODED:$password"
    }
}
