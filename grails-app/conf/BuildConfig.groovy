import grails.util.Environment

grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

//grails.project.fork = [
//    // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
//    //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
//
//    // configure settings for the test-app JVM, uses the daemon by default
//    test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
//    // configure settings for the run-app JVM
//    run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
//    // configure settings for the run-war JVM
//    war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
//    // configure settings for the Console UI JVM
//    console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
//]

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
        grailsCentral()

        mavenLocal()
        mavenCentral()

        mavenRepo "https://repository-kobo.forge.cloudbees.com/snapshot/"
        mavenRepo "https://repository-kobo.forge.cloudbees.com/release/"
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.

        runtime "postgresql:postgresql:9.0-801.jdbc4"

        compile "org.jggug.kobo:gircbot:0.1-SNAPSHOT"

        compile 'log4j:apache-log4j-extras:1.1', { // for logging
            excludes 'log4j'
        }
    }

    plugins {
        runtime ":hibernate:3.6.10.1" // or ":hibernate4:4.1.11.1"
        runtime ":jquery:1.10.2"
        runtime ":jquery-ui:1.8.15"

        compile ":spring-security-core:1.2.7.3"
        compile ":quartz:1.0"
        compile ":scaffolding:2.0.0"
        compile ':cache:1.1.1'
        runtime ":database-migration:1.3.5"

        runtime ":resources:1.2"
        if (Environment.current == Environment.PRODUCTION) {
            runtime ":zipped-resources:1.0.1"
            runtime ":cached-resources:1.1"
            runtime ":yui-minify-resources:0.1.5"
        }
        compile ":lesscss-resources:1.3.3"
        compile ":coffeescript-resources:0.3.8"

        build ":tomcat:7.0.42"

        if (Environment.current == Environment.DEVELOPMENT) {
            build ":console-enhancements:1.0"
            build ":improx:0.3"
            compile "org.jggug.kobo:request-tracelog:0.2"
        }
    }
}

