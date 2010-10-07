security {

    /** misc properties */

    active = true

    registerLoggerListener = true

    // user and role class properties
    userLookup.userDomainClassName = 'Person'
    userLookup.usernamePropertyName = 'loginName'
    userLookup.enabledPropertyName = 'enabled'
    userLookup.passwordPropertyName = 'password'
    userLookup.authoritiesPropertyName = 'roles'
//    userLookup.accountExpiredPropertyName = 'accountExpired'
//    userLookup.accountLockedPropertyName = 'accountLocked'
//    userLookup.passwordExpiredPropertyName = 'passwordExpired'
//    userLookup.authorityJoinClassName = 'PersonAuthority'
    authority.className = 'Role'
    authority.nameField = 'name'

    // failureHandler
    failureHandler.defaultFailureUrl = '/login/denied'

    // successHandler
    successHandler.defaultTargetUrl = '/login/auth'


    /** passwordEncoder */
    // see http://java.sun.com/j2se/1.5.0/docs/guide/security/CryptoSpec.html#AppA
    password.algorithm = 'MD5'
    password.encodeHashAsBase64 = false

    /** use RequestMap from DomainClass */
    useRequestMapDomainClass = false
    requestMapString = """
        CONVERT_URL_TO_LOWERCASE_BEFORE_COMPARISON
        PATTERN_TYPE_APACHE_ANT

        /channel/index/**=IS_AUTHENTICATED_ANONYMOUSLY
        /channel/list/**=IS_AUTHENTICATED_ANONYMOUSLY
        /channel/show/**=IS_AUTHENTICATED_ANONYMOUSLY
        /channel/kick/**=ROLE_ADMIN
        /channel/**=ROLE_USER,ROLE_ADMIN
        /register/create/**=IS_AUTHENTICATED_ANONYMOUSLY
        /register/save/**=IS_AUTHENTICATED_ANONYMOUSLY
        /register/**=ROLE_USER,ROLE_ADMIN
        /person/**=ROLE_ADMIN
        /**=IS_AUTHENTICATED_ANONYMOUSLY
    """

    /** default user's role for user registration */
    defaultRole = 'ROLE_USER'
}
