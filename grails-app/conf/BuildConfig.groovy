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
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

    repositories {
        inherits true // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()
        mavenLocal()
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        runtime "postgresql:postgresql:9.0-801.jdbc4"
    }

    plugins {
        runtime ":hibernate:$grailsVersion"
        runtime ":resources:1.1.6"
        runtime ":jquery:1.7.1"
        runtime ":jquery-ui:1.8.15"
        compile ":spring-security-core:1.2.1"
        compile ":quartz:0.4.2"
        //test ":auto-test:1.0.1"
        compile ":spock:0.6"
        build ":tomcat:$grailsVersion"

        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.4"
    }
}
grails.plugin.location."request-tracelog" = "./plugins/request-tracelog-0.3"

