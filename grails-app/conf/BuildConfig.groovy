import grails.util.Environment

grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.fork = [
    // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
    //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

    // configure settings for the test-app JVM, uses the daemon by default
    test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
    // configure settings for the run-app JVM
    run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the run-war JVM
    war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the Console UI JVM
    console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        mavenLocal()
        grailsCentral()
        mavenCentral()

        mavenRepo "http://dl.bintray.com/nobeans/maven"
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.

        runtime "postgresql:postgresql:9.0-801.jdbc4"

        compile "org.jggug.kobo:gircbot:0.2"

        compile 'log4j:apache-log4j-extras:1.1', { // for logging
            excludes 'log4j'
        }

        compile "io.vertx:vertx-core:2.0.2-final"
        compile 'io.vertx:lang-groovy:2.0.0-final'
    }

    plugins {
        // plugins for the build system only
        build ":tomcat:7.0.55"

        // plugins for the compile step
        compile ":scaffolding:2.1.2"
        compile ':cache:1.1.8'
        compile ":asset-pipeline:2.0.18"
        compile ":less-asset-pipeline:2.0.8"
        compile ":coffee-asset-pipeline:2.0.6"
        compile ":quartz:1.0"
        compile ":spring-security-core:2.0-RC4"

        // plugins needed at runtime but not for compilation
        runtime ":hibernate4:4.3.6.1" // or ":hibernate:3.6.10.18"
        runtime ":database-migration:1.4.0"
        runtime ":jquery:1.11.1"
        runtime ":jquery-ui:1.10.4"

        if (Environment.developmentMode) {
            build ":improx:0.3"
            compile "org.jggug.kobo:request-tracelog:0.4"
        }
    }
}

