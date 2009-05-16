// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text-plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]
// The default codec used to encode data with ${}
grails.views.default.codec="none" // none, html, base64
grails.views.gsp.encoding="UTF-8"
grails.converters.encoding="UTF-8"

// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true

// set per-environment serverURL stem for creating absolute links
environments {
    production {
        grails.serverURL = "http://www.changeme.com"
    }
}

// log4
log4j = {
    appenders {
        console      name:'stdout',
                     layout:pattern(conversionPattern: '%d{yyyy-MMM-dd HH:mm:ss,SSS} [%p] (%c{2}) %m%n')
        rollingFile  name:"file", file:"log/irclog.log", maxFileSize:'10MB', maxBackupIndex:5,
                     layout:pattern(conversionPattern: '%d{yyyy-MMM-dd HH:mm:ss,SSS} [%p] (%c{2}) %m%n')
        rollingFile  name:'stacktrace', file:'log/stacktrace.log', maxFileSize:'10MB', maxBackupIndex:5,
                     layout:pattern(conversionPattern: '%d{yyyy-MMM-dd HH:mm:ss,SSS} [%p] (%c{2}) %m%n')
    }
    root {
        info 'stdout', 'file'
        additivity = false
    }
    error 'org.codehaus.groovy.grails.web.servlet'  //  controllers
    error 'org.codehaus.groovy.grails.web.pages' //  GSP
    error 'org.codehaus.groovy.grails.web.sitemesh' //  layouts
    error 'org.codehaus.groovy.grails.web.mapping.filter' // URL mapping
    error 'org.codehaus.groovy.grails.web.mapping' // URL mapping
    error 'org.codehaus.groovy.grails.commons' // core / classloading
    error 'org.codehaus.groovy.grails.plugins' // plugins
    error 'org.codehaus.groovy.grails.orm.hibernate' // hibernate integration
    error 'org.springframework'
    info  'org.springframework.security' // Acegi plugin
    error 'org.hibernate'
    warn  'org.mortbay.log'

    // for My application
    info "grails.app.controller"
    info "grails.app.service"
    info "grails.app.task"
}

// irclog-viewer
irclog {
    viewer.defaultMax = 100
    viewer.typeVisible = true
    viewer.defaultTargetUrl = "/summary"
    session.maxInactiveInterval = 24 * 60 * 60 // [sec] => 1day
}

