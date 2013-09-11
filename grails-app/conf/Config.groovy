// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

grails.config.locations = [
    "file:${userHome}/.grails/${appName}-config.properties",
    "file:${userHome}/.grails/${appName}-config.groovy",
]

grails.project.groupId = 'irclog' // change this to alter the default package name and Maven publishing destination
// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
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
    hal:           ['application/hal+json','application/hal+xml'],
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}

grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = ''

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart = false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

// auto flush
//grails.gorm.autoFlush = true

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

// data binding
// for compatibility for 2.2.x
grails.databinding.trimStrings = false
grails.databinding.convertEmptyStringsToNull = false

// database migration
grails.plugin.databasemigration.updateOnStart = true
grails.plugin.databasemigration.updateOnStartFileNames = ["changelog.groovy"]

// log4j configuration
import grails.plugins.springsecurity.SecurityConfigType
import org.apache.log4j.rolling.RollingFileAppender
import org.apache.log4j.rolling.TimeBasedRollingPolicy

log4j = {
    def createRollingFile = { name, dir, fileName, conversionPattern = '%d{yyyy-MM-dd HH:mm:ss,SSS} [%p] (%c) %m%n' ->
        def rollingPolicy = new TimeBasedRollingPolicy(fileNamePattern: "${dir}/${fileName}.%d{yyyy-MM-dd}.log")
        rollingPolicy.activateOptions()
        return new RollingFileAppender(
            name: name,
            layout: pattern(conversionPattern: conversionPattern),
            rollingPolicy: rollingPolicy
        )
    }

    appenders {
        def logDir = 'log'
        environments {
            production {
                logDir = "/var/log/${appName}"
            }
        }
        appender createRollingFile('operation', logDir, 'operation', '%d{yyyy-MM-dd HH:mm:ss,SSS} %m%n')
        appender createRollingFile('application', logDir, 'application')
        appender createRollingFile('stacktrace', logDir, 'stacktrace')
        console name: 'stdout', layout: pattern(conversionPattern: '%d{yyyy-MM-dd HH:mm:ss,SSS} [%p] (%c{1}) %m%n')
    }

    // default
    error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
        'org.codehaus.groovy.grails.web.pages', //  GSP
        'org.codehaus.groovy.grails.web.sitemesh', //  layouts
        'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
        'org.codehaus.groovy.grails.web.mapping', // URL mapping
        'org.codehaus.groovy.grails.commons', // core / classloading
        'org.codehaus.groovy.grails.plugins', // plugins
        'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
        'org.springframework',
        'org.hibernate',
        'net.sf.ehcache.hibernate',
        'grails.app.services.org.grails.plugin.resource',
        'grails.app.taglib.org.grails.plugin.resource',
        'grails.app.resourceMappers.org.grails.plugin.resource',
        'grails.app.services.NavigationService'

    // for SQL
    // http://yamkazu.hatenablog.com/entry/2012/10/20/133945
    if (Boolean.valueOf(System.properties['debug.sql'])) {
        trace 'org.hibernate.type.descriptor.sql.BasicBinder'
        debug 'org.hibernate.SQL'
        debug 'groovy.sql.Sql'
    }

    // for resources plugin
    //debug 'org.grails.plugin.resource'

    // for quartz plugin
    debug 'grails.app.jobs'

    environments {
        ['development', 'test'].each { env ->
            "$env" {
                root {
                    info 'application', 'stdout'
                }

                debug 'grails.app',
                    'irclog',
                    'grails.app.filters.RequestTracelogFilters',
                    'org.jggug.kobo.gircbot'
            }
        }
        production {
            root {
                info 'application'
            }

            info 'grails.app',
                'irclog',
                'grails.app.filters.RequestTracelogFilters',
                'org.jggug.kobo.gircbot'

            // unfortunately grails would write a log ERROR level just when SQL error ocurring, so set to off.
            // but it keeps as default at development/test for debugging.
            off 'org.codehaus.groovy.grails.orm.hibernate.events.PatchedDefaultFlushEventListener'
            off 'org.hibernate.util.JDBCExceptionReporter'
        }
    }
}

// for quartz plugin
quartz.autoStartup = true
environments {
    test {
        quartz.autoStartup = false
    }
}

// irclog-viewer
import org.jggug.kobo.gircbot.reactors.Debugger
import org.jggug.kobo.gircbot.reactors.Dictionary
import org.jggug.kobo.gircbot.reactors.InviteAndByeResponder
import org.jggug.kobo.gircbot.reactors.Logger
import org.jggug.kobo.gircbot.reactors.OpDistributor
import org.jggug.kobo.gircbot.jobs.Reminder
import irclog.ircbot.IrclogLogAppender

irclog {
    viewer {
        defaultMax = 100
        typeVisible = true
        defaultTargetUrl = "/summary"
    }

    ircbot {
        enable = true
        limitOfSavedStates = 7
        server {
            host = "localhost"
            port = 6667
        }
        nick = "ircbot"
        channel {
            autoJoinTo = ["LATEST_SAVED_CHANNELS", "#test1", "#test2"]
        }
        reactors = [
            new Dictionary(new File("${userHome}/.gircbot-dictionary")),
            new OpDistributor(),
            new InviteAndByeResponder(),
            new Logger(new IrclogLogAppender("#lounge")),
        ]
        environments {
            development {
                reactors << new Debugger()
            }
        }
        jobs = [
            new Reminder(new File("${userHome}/.gircbot-reminder")),
        ]
    }
}

//------------------------------------------
// SpringSecurity
//------------------------------------------

// user and role class properties
grails.plugins.springsecurity.userLookup.userDomainClassName = 'irclog.Person'
grails.plugins.springsecurity.userLookup.usernamePropertyName = 'loginName'
grails.plugins.springsecurity.userLookup.passwordPropertyName = 'password'
grails.plugins.springsecurity.userLookup.authoritiesPropertyName = 'roles'
grails.plugins.springsecurity.authority.className = 'irclog.Role'
grails.plugins.springsecurity.authority.nameField = 'name'

// passwordEncoder
grails.plugins.springsecurity.password.algorithm = 'MD5' // FIXME deprecated...
grails.plugins.springsecurity.password.encodeHashAsBase64 = false

// use RequestMap from DomainClass
grails.plugins.springsecurity.securityConfigType = SecurityConfigType.InterceptUrlMap
grails.plugins.springsecurity.interceptUrlMap = [
    '/channel/index/**': ['permitAll'],
    '/channel/list/**': ['permitAll'],
    '/channel/show/**': ['permitAll'],
    '/channel/kick/**': ['hasRole("ROLE_ADMIN")'],
    '/channel/**': ['hasRole("ROLE_USER")'],
    '/register/create/**': ['permitAll'],
    '/register/save/**': ['permitAll'],
    '/register/**': ['hasRole("ROLE_USER")'],
    '/person/**': ['hasRole("ROLE_ADMIN")'],
    '/**': ['permitAll']
]
grails.plugins.springsecurity.roleHierarchy = '''
   ROLE_ADMIN > ROLE_USER
'''
