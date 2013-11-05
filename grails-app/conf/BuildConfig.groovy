import grails.util.Environment

grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

// uncomment (and adjust settings) to fork the JVM to isolate classpaths
//grails.project.fork = [
//   run: [maxMemory:1024, minMemory:64, debug:false, maxPerm:256]
//]

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

        test "org.spockframework:spock-grails-support:0.7-groovy-2.0"
    }

    plugins {
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.8.3"
        runtime ":resources:1.2"
        runtime ":jquery-ui:1.8.15"
        compile ":spring-security-core:1.2.7.3"
        compile ":quartz:1.0"

        test(":spock:0.7") {
            exclude "spock-grails-support"
        }

        if (Environment.current == Environment.PRODUCTION) {
            runtime ":zipped-resources:1.0"
            runtime ":cached-resources:1.0"
            runtime ":yui-minify-resources:0.1.5"
        }
        compile ":lesscss-resources:1.3.3"
        compile ":coffeescript-resources:0.3.8"

        build ":tomcat:$grailsVersion"

        runtime ":database-migration:1.3.2"

        compile ':cache:1.0.1'

        if (Environment.current == Environment.DEVELOPMENT) {
            build ":console-enhancements:1.0"
            build ":improx:0.3"
            compile "org.jggug.kobo:request-tracelog:0.2"
        }
    }
}

