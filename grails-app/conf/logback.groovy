import grails.util.BuildSettings
import grails.util.Environment

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = /%d{yyyy-MM-dd HH:mm:ss.SSS} [%level] \(%thread\) \(%logger{1}:%line\) -- %msg%n/
    }
}

root info, ['STDOUT']

if (Environment.current == Environment.DEVELOPMENT) {
    def targetDir = BuildSettings.TARGET_DIR
    if (targetDir) {
        appender("FULL_STACKTRACE", FileAppender) {
            file = "${targetDir}/stacktrace.log"
            append = true
            encoder(PatternLayoutEncoder) {
                pattern = "%level %logger - %msg%n"
            }
        }
        logger "StackTrace", ERROR, ['FULL_STACKTRACE'], false
    }
}
