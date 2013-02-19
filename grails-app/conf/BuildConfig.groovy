grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

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
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        runtime "postgresql:postgresql:9.0-801.jdbc4"

        build "org.jggug.kobo:gircbot:0.1-SNAPSHOT"

        compile 'log4j:apache-log4j-extras:1.1', { // for logging
            excludes 'log4j'
        }

        test "org.spockframework:spock-grails-support:0.7-groovy-2.0"
    }

    plugins {
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.8.0"
        runtime ":resources:1.1.6"
        runtime ":jquery-ui:1.8.15"
        compile ":spring-security-core:1.2.7.3"
        compile ":quartz:1.0-RC2"

        test(":spock:0.7") {
          exclude "spock-grails-support"
        }

        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.4"

        build ":tomcat:$grailsVersion"

        runtime ":database-migration:1.2.2"

        compile ':cache:1.0.1'

        build ":improx:0.2"
    }
}
grails.plugin.location."request-tracelog" = "./plugins/request-tracelog"

