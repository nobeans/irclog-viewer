// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = 'irclog' // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: [
        'text/html',
        'application/xhtml+xml'
    ],
    xml: [
        'text/xml',
        'application/xml'
    ],
    text: 'text/plain',
    js: 'text/javascript',
    rss: 'application/rss+xml',
    atom: 'application/atom+xml',
    css: 'text/css',
    csv: 'text/csv',
    all: '*/*',
    json: [
        'application/json',
        'text/json'
    ],
    form: 'application/x-www-form-urlencoded',
    multipartForm: 'multipart/form-data'
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = ''

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []

// set per-environment serverURL stem for creating absolute links
environments {
    production { grails.serverURL = "http://www.changeme.com" }
    development { grails.serverURL = "http://localhost:8080/${appName}" }
    test { grails.serverURL = "http://localhost:8080/${appName}" }
}

// log4j configuration
log4j = {
    appenders {
        console      name:'stdout',
                        layout:pattern(conversionPattern: '%d{yyyy-MMM-dd HH:mm:ss,SSS} [%p] (%c{1}) %m%n')
        rollingFile  name:"file", file:"log/irclog.log", maxFileSize:'10MB', maxBackupIndex:5,
                        layout:pattern(conversionPattern: '%d{yyyy-MMM-dd HH:mm:ss,SSS} [%p] (%c{2}) %m%n')
        rollingFile  name:'stacktrace', file:'log/stacktrace.log', maxFileSize:'10MB', maxBackupIndex:5,
                        layout:pattern(conversionPattern: '%d{yyyy-MMM-dd HH:mm:ss,SSS} [%p] (%c{2}) %m%n')
    }
    root {
        info 'stdout', 'file'
        additivity = false
    }
    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
                    'org.codehaus.groovy.grails.web.pages', //  GSP
                    'org.codehaus.groovy.grails.web.sitemesh', //  layouts
                    'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
                    'org.codehaus.groovy.grails.web.mapping', // URL mapping
                    'org.codehaus.groovy.grails.commons', // core / classloading
                    'org.codehaus.groovy.grails.plugins', // plugins
                    'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
                    'org.springframework',
                    'org.hibernate',
                    'net.sf.ehcache.hibernate'
    warn   'org.mortbay.log'
    
    // for My application
    info "grails.app.controller"
    info "grails.app.service"
    info "grails.app.task"
    debug "grails.app.filters.RequestTracelogFilters"
}

// irclog-viewer
irclog {
    viewer.defaultMax = 100
    viewer.typeVisible = true
    viewer.defaultTargetUrl = "/summary"
    session.maxInactiveInterval = 24 * 60 * 60 // [sec] => 1day

    /** default user's role for user registration */
    security.defaultRole = 'ROLE_USER'
}

// SpringSecurity ------------------------------------------
import grails.plugins.springsecurity.SecurityConfigType

// user and role class properties
grails.plugins.springsecurity.userLookup.userDomainClassName = 'Person'
grails.plugins.springsecurity.userLookup.usernamePropertyName = 'loginName'
grails.plugins.springsecurity.userLookup.enabledPropertyName = 'enabled'
grails.plugins.springsecurity.userLookup.passwordPropertyName = 'password'
grails.plugins.springsecurity.userLookup.authoritiesPropertyName = 'roles'
grails.plugins.springsecurity.userLookup.accountExpiredPropertyName = 'accountExpired'
grails.plugins.springsecurity.userLookup.accountLockedPropertyName = 'accountLocked'
grails.plugins.springsecurity.userLookup.passwordExpiredPropertyName = 'passwordExpired'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'PersonAuthority'
grails.plugins.springsecurity.authority.className = 'Role'
grails.plugins.springsecurity.authority.nameField = 'name'

// failureHandler
grails.plugins.springsecurity.failureHandler.defaultFailureUrl = '/login/denied'

// successHandler
grails.plugins.springsecurity.successHandler.defaultTargetUrl = '/login/auth'

/** passwordEncoder */
grails.plugins.springsecurity.password.algorithm = 'MD5'
grails.plugins.springsecurity.password.encodeHashAsBase64 = false

/** use RequestMap from DomainClass */
grails.plugins.springsecurity.securityConfigType = SecurityConfigType.InterceptUrlMap
grails.plugins.springsecurity.interceptUrlMap = [
    '/channel/index/**':    ['permitAll'],
    '/channel/list/**':     ['permitAll'],
    '/channel/show/**':     ['permitAll'],
    '/channel/kick/**':     ['hasRole("ROLE_ADMIN")'],
    '/channel/**':          ['hasAnyRole("ROLE_USER","ROLE_ADMIN")'],
    '/register/create/**':  ['permitAll'],
    '/register/save/**':    ['permitAll'],
    '/register/**':         ['hasAnyRole("ROLE_USER","ROLE_ADMIN")'],
    '/person/**':           ['hasRole("ROLE_ADMIN")'],
    '/**':                  ['permitAll']
]
