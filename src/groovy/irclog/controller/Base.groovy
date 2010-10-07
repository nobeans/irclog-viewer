package irclog.controller

import org.codehaus.groovy.grails.commons.ApplicationHolder
//import org.grails.plugins.springsecurity.service.AuthenticateService
import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.springframework.web.servlet.support.RequestContextUtils as RCU

abstract class Base {
    
    /** Configuration */
    def config = ApplicationHolder.application.config
    
    /** Authenticate Service */
    //    AuthenticateService authenticateService
    def authenticationTrustResolver
    def springSecurityService
    
    /** Login user */
    def loginUserName
    def loginUserDomain
    def loginUserRole
    
    /** is user logged on or not */
    boolean isLoggedIn
    
    /** principal */
    def authPrincipal
    
    /** Role */
    boolean isAdmin
    boolean isUser
    
    def beforeInterceptor = {
        if (springSecurityService.isLoggedIn()) {
            log.error('request not allowed')
            redirect(uri: '/')
            return
        }
        
        authPrincipal = SCH?.context?.authentication?.principal
        if (authPrincipal != null && authPrincipal != 'anonymousUser') {
            isLoggedIn = true
            
            // 各種ログインユーザ情報を取得する。リクエスト属性にも格納しておく。
            loginUserName = authPrincipal?.username
            loginUserDomain = authenticateService.userDomain()
            loginUserRole = CollectionUtils.getFirstOrNull(loginUserDomain?.roles)
            request.loginUserName = loginUserName
            request.loginUserDomain = loginUserDomain
            request.loginUserRole = loginUserRole
            
            // 現状はロールが増えるごとに修正が必要。
            isAdmin = authenticateService.ifAnyGranted('ROLE_admin')
            isUser = authenticateService.ifAnyGranted('ROLE_user')
        }
    }
}
