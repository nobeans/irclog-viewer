// See http://logback.qos.ch/manual/groovy.html for details on configuration
import grails.util.Environment

def FILE_LOG_PATTERN = '%d{yyyy-MM-dd HH:mm:ss.SSS}\t%thread\t%level\t%logger:%line\t%m%n'
def CONSOLE_LOG_PATTERN = /%d{HH:mm:ss.SSS} [%thread] %highlight(%level) %cyan(\(%logger{39}:%line\)) %m%n/

def logDir = 'logs'

appender('STDOUT', ConsoleAppender) {
    withJansi = true
    encoder(PatternLayoutEncoder) {
        pattern = CONSOLE_LOG_PATTERN
    }
}

appender("FILE", RollingFileAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = FILE_LOG_PATTERN
    }
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${logDir}/application.%d{yyyy-MM-dd}.log"
    }
}

appender("FULL_STACKTRACE", RollingFileAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = FILE_LOG_PATTERN
    }
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${logDir}/stacktrace.%d{yyyy-MM-dd}.log"
    }
}

root INFO, ['STDOUT', 'FILE']

Environment.executeForCurrentEnvironment {
    development {
        logger "StackTrace", ERROR, ['FULL_STACKTRACE'], false
    }
    test {
        logger "StackTrace", ERROR, ['FULL_STACKTRACE'], false
    }
}

logger "org.springframework.security.web.authentication.rememberme", DEBUG, ['STDOUT', 'FILE']
