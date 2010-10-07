package irclog.controller

import grails.plugins.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import irclog.utils.CollectionUtils

abstract class Base {
    
    /** Configuration */
    def config = ApplicationHolder.application.config
    
    /** Authenticate Service */
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
        
        authPrincipal = springSecurityService.authentication?.principal
        if (authPrincipal != null && authPrincipal != 'anonymousUser') {
            isLoggedIn = true
            
            // 各種ログインユーザ情報を取得する。リクエスト属性にも格納しておく。
            loginUserName = authPrincipal?.username
            loginUserDomain = springSecurityService.userDomain()
            loginUserRole = CollectionUtils.getFirstOrNull(loginUserDomain?.roles)
            request.loginUserName = loginUserName
            request.loginUserDomain = loginUserDomain
            request.loginUserRole = loginUserRole
            
            // 現状はロールが増えるごとに修正が必要。
            isAdmin = SpringSecurityUtils.ifAnyGranted('ROLE_admin')
            isUser = SpringSecurityUtils.ifAnyGranted('ROLE_user')
        }
    }
}
