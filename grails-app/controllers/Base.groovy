import grails.util.GrailsUtil
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.grails.plugins.springsecurity.service.AuthenticateService
import org.springframework.security.context.SecurityContextHolder as SCH
import org.springframework.web.servlet.support.RequestContextUtils as RCU

abstract class Base {

    /** Configuration */
    def config = ApplicationHolder.application.config

    /** Authenticate Service */
    AuthenticateService authenticateService

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

    /** コントローラ側の処理時間計測用(レスポンスタイムではないことに注意) */
    def startTime

    def beforeInterceptor = {
        /* for DEBUG */
        if (System.getProperty("grails.env") == "development") {
            startTime = System.currentTimeMillis()
            request.startTime = startTime // フッタのレスポンスタイム表示用
            log.info("BEGIN Request " + ">"*30)
        }

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

        /* for DEBUG */
        if (GrailsUtil.isDevelopmentEnv()) {
            println "[Before]" + "="*20
            println "Request attributes:"
            request.getAttributeNames().collect({it}).sort().each { println "  ${it} = ${request.getAttribute(it)}" }
            println "-"*20
            println "Request parameters:"
            params.keySet().sort().each { println "  ${it} = ${params[it]}" }
            println "-"*20
            println "Session attributes:"
            session.getAttributeNames().collect({it}).sort().each { println "  ${it} = ${session.getAttribute(it)}" }
            println "="*20
        }

        /* cache */
        nocache(response)
    }

    def afterInterceptor = {
        /* for DEBUG */
        if (GrailsUtil.isDevelopmentEnv()) {
            println "[After]" + "="*20
            println "Session attributes:"
            session.getAttributeNames().collect({it}).sort().each { println "  ${it} = ${session.getAttribute(it)}" }
            println "="*20
            def time = (System.currentTimeMillis() - startTime) / 1000.0
            log.info("END Request " + "<"*30 + " (control time: ${time}[sec])")
        }
    }

    /** cache controls */
    private void nocache(response) {
        response.setHeader('Cache-Control', 'no-cache') // HTTP 1.1
        response.addDateHeader('Expires', 0)
        response.setDateHeader('max-age', 0)
        response.setIntHeader ('Expires', -1) //prevents caching at the proxy server
        response.addHeader('cache-Control', 'private') //IE5.x only
    }
}
