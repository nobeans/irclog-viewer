// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = 'irclog' // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [
    all:           '*/*',
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']
grails.resources.modules = {
    common {
        dependsOn 'jquery'
        resource '/js/application.js'
        resource '/js/common.js'
        resource '/js/jquery_kill_referrer.js'
    }
    singleViewer {
        dependsOn 'jquery-ui'
        resource '/js/singleViewer.js'
    }
    mixedViewer {
        dependsOn 'jquery-ui'
        resource '/js/mixedViewer.js'
        resource '/js/jquery.highlight-3.js'
    }
    channel {
        resource '/js/channel.js'
    }
}

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
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

// auto flush
grails.gorm.autoFlush = true

// set per-environment serverURL stem for creating absolute links
environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

// log4j configuration
log4j = {
    appenders {
        console     name:'stdout',
                    layout:pattern(conversionPattern: '%d{yyyy-MMM-dd HH:mm:ss,SSS} [%p] (%c{1}) %m%n')
        rollingFile name:'file',
                    file:'log/irclog.log', maxFileSize:'10MB', maxBackupIndex:5,
                    layout:pattern(conversionPattern: '%d{yyyy-MMM-dd HH:mm:ss,SSS} [%p] (%c{2}) %m%n')
        rollingFile name:'stacktrace',
                    file:'log/stacktrace.log', maxFileSize:'10MB', maxBackupIndex:5,
                    layout:pattern(conversionPattern: '%d{yyyy-MMM-dd HH:mm:ss,SSS} [%p] (%c{2}) %m%n')
    }

    root {
        info 'stdout', 'file'
    }

    warn 'org.codehaus.groovy.grails.web.servlet',        //  controllers
         'org.codehaus.groovy.grails.web.pages',          //  GSP
         'org.codehaus.groovy.grails.web.sitemesh',       //  layouts
         'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
         'org.codehaus.groovy.grails.web.mapping',        // URL mapping
         'org.codehaus.groovy.grails.commons',            // core / classloading
         'org.codehaus.groovy.grails.plugins',            // plugins
         'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
         'org.springframework',
         'org.hibernate',
         'net.sf.ehcache.hibernate'

    development {
        debug 'grails.app.controller',
              'grails.app.service',
              'grails.app.filter',
              'grails.app.views',
              'irclog',
              'grails.app.filters.RequestTracelogFilters'
    }
    production {
        info  'grails.app.controller',
              'grails.app.service',
              'grails.app.filter',
              'grails.app.views',
              'irclog',
              'grails.app.filters.RequestTracelogFilters'
    }
}

// irclog-viewer
irclog {
    viewer.defaultMax = 100
    viewer.typeVisible = true
    viewer.defaultTargetUrl = "/summary"
}

//------------------------------------------
// SpringSecurity
//------------------------------------------
import grails.plugins.springsecurity.SecurityConfigType

// user and role class properties
grails.plugins.springsecurity.userLookup.userDomainClassName = 'irclog.Person'
grails.plugins.springsecurity.userLookup.usernamePropertyName = 'loginName'
grails.plugins.springsecurity.userLookup.passwordPropertyName = 'password'
grails.plugins.springsecurity.userLookup.authoritiesPropertyName = 'roles'
grails.plugins.springsecurity.authority.className = 'irclog.Role'
grails.plugins.springsecurity.authority.nameField = 'name'

// failureHandler
grails.plugins.springsecurity.failureHandler.defaultFailureUrl = '/login/denied'

// successHandler
grails.plugins.springsecurity.successHandler.defaultTargetUrl = '/login/auth'

// passwordEncoder
grails.plugins.springsecurity.password.algorithm = 'MD5'
grails.plugins.springsecurity.password.encodeHashAsBase64 = false

// use RequestMap from DomainClass
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
