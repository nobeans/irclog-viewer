package irclog

import irclog.security.SpringSecurityContext
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.web.WebAttributes

class LoginController implements SpringSecurityContext {

    static defaultAction = 'auth'

    def auth() {
        if (loggedIn) {
            redirect uri: '/'
            return
        }
        render view: 'auth'
    }

    def authfail() {
        render view: 'auth', model: [errorMessage: errorMessage]
    }

    private getErrorMessage() {
        def exception = session[WebAttributes.AUTHENTICATION_EXCEPTION]
        if (!exception) {
            return null
        }
        if (exception instanceof AccountExpiredException) {
            return message(code: 'springSecurity.errors.login.expired')
        }
        if (exception instanceof CredentialsExpiredException) {
            return message(code: 'springSecurity.errors.login.passwordExpired')
        }
        if (exception instanceof DisabledException) {
            return message(code: 'springSecurity.errors.login.disabled')
        }
        if (exception instanceof LockedException) {
            return message(code: 'springSecurity.errors.login.locked')
        }
        return message(code: 'springSecurity.errors.login.fail')
    }

    def denied() {
        render view: 'denied'
    }
}
