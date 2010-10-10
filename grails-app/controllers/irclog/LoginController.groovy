package irclog

import irclog.controller.Base

import org.springframework.security.authentication.DisabledException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class LoginController extends Base {

    def index = {
        redirect action:auth, params:params
    }

    /**
     * Show the login page.
     */
    def auth = {
        if (request.isLoggedIn) {
            flash.message = null
            flash.errors = null
            
            // セッション有効期間をカスタマイズ
            session.maxInactiveInterval = ConfigurationHolder.config.irclog.session.maxInactiveInterval
            
            log.info "Logged in by ${request.loginUserName}"
            redirect(uri: ConfigurationHolder.config.irclog.viewer.defaultTargetUrl)
        }
        else {
            render(view:'auth', params:params)
        }
    }

    /** アクセス不許可 */
    def denied = {
        log.warn "Denied to access: ${request['javax.servlet.forward.request_uri']} by ${request.loginUserName}"
        def statusCode = request['javax.servlet.error.status_code']
        if (statusCode) {
            flash.errors = ['login.accessDenied.error.' + statusCode]
        } else {
            flash.errors = ['login.accessDenied.error']
        }
        redirect(uri: ConfigurationHolder.config.irclog.viewer.defaultTargetUrl)
    }

    /**
     * Callback after a failed login. Redirects to the auth page with a warning message.
     */
    def authfail = {
        def loginName = session[UsernamePasswordAuthenticationFilter.SPRING_SECURITY_LAST_USERNAME_KEY]
        def exception = session[AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY]
        if (exception) {
            if (exception instanceof DisabledException) {
                flash.message = "login.loginName.disabled.error"
            }
            else {
                if (loginName == "") {
                    flash.message = "login.loginName.empty.error"
                } else {
                    flash.message = "login.loginName.invalid.error"
                }
            }
        }
        flash.loginName = loginName
        render(view: 'auth')
    }
}
