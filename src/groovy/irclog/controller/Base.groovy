package irclog.controller

import org.codehaus.groovy.grails.commons.ApplicationHolder
//import org.grails.plugins.springsecurity.service.AuthenticateService
//import org.springframework.security.context.SecurityContextHolder as SCH
import org.springframework.web.servlet.support.RequestContextUtils as RCU

abstract class Base {
    
    /** Configuration */
    def config = ApplicationHolder.application.config
    
    /** Authenticate Service */
    //    AuthenticateService authenticateService
    
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
    
    /** Locale */
    Locale locale
    
    /** main request permission setting */
    def requestAllowed
    
    def beforeInterceptor = {
        if (requestAllowed != null && !authenticateService.ifAnyGranted(requestAllowed)) {
            log.error('request not allowed: ' + requestAllowed)
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
        
        /* i18n: if lang params */
        if (params['lang']) {
            locale = new Locale(params['lang'])
            RCU.getLocaleResolver(request).setLocale(request,response,locale)
            session.lang = params['lang']
        }
        /* need this for jetty */
        if (session.lang != null) {
            locale = new Locale(session.lang)
            RCU.getLocaleResolver(request).setLocale(request,response,locale)
        }
        if (locale == null) {
            locale = RCU.getLocale(request)
        }
    }
}
