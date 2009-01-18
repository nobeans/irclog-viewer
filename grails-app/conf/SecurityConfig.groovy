security {

    active = true

    // change to true to use OpenID authentication
    useOpenId = false

    // change to true and specify parameters to use LDAP/ActiveDirectory authentication
    useLdap = false

    algorithm = 'MD5' 
    //use Base64 text ( true or false )
    encodeHashAsBase64 = false
    errorPage = '/viewer/index'

    /** login user domain class name and fields */
    loginUserDomainClass = "Person"
    userName = 'loginName'
    password = 'password'
    enabled = 'enabled'
    relationalAuthorities = 'roles'

    /*
     * You can specify method to retrieve the roles. (you need to set relationalAuthorities = null)
     */
    // getAuthoritiesMethod = null //'getMoreAuthorities'

    /**
     * Authority domain class authority field name
     * authorityFieldInList
     */
    authorityDomainClass = "Role"
    authorityField = 'name'

	/** rememberMeServices */
	cookieName = 'grails_remember_me' 
	alwaysRemember = true
	tokenValiditySeconds = 1209600 //14 days
	parameter = '_spring_security_remember_me'
	rememberMeKey = 'grailsRocks'

	/** LoggerListener 
	 * ( add 'log4j.logger.org.springframework.security=info,stdout'
	 * to log4j.*.properties to see logs )
	 */
	useLogger = true

    /** use RequestMap from DomainClass */
    useRequestMapDomainClass = false
    requestMapString = """
        CONVERT_URL_TO_LOWERCASE_BEFORE_COMPARISON
        PATTERN_TYPE_APACHE_ANT

        /**=IS_AUTHENTICATED_ANONYMOUSLY
        /channel/delete/**=ROLE_ADMIN
        /channel/create/**=IS_AUTHENTICATED_FULLY
        /channel/edit/**=IS_AUTHENTICATED_FULLY
        /channel/join/**=IS_AUTHENTICATED_FULLY
    """

    /**
     * To use email notification for user registration, set the following userMail to
     * true and config your mail settings.Note you also need to run the script
     * grails generate-registration.
     */
    useMail = false

    /** AJAX request header */
    //ajaxHeader = 'X-Requested-With'
  
    /** default user's role for user registration */
    defaultRole = 'ROLE_USER'

    /** use basicProcessingFilter */
    basicProcessingFilter = false
    /** use switchUserProcessingFilter */
    switchUserProcessingFilter = false

    /** redirect URL when auth is success */
    // MEMO:
    // 認証処理の一環としてflashをクリアしてから、トップページに遷移させたいためここでは認証コントローラのURLとしている。
    // どちらかというと、Acegiのフィルタのバグであるような気がするのだが・・・。
    defaultTargetUrl = "/login/auth"
}
