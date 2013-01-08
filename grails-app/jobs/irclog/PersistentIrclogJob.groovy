package irclog

import grails.util.Environment

class PersistentIrclogJob {

    def concurrent = false

    static triggers = {
        if (Environment.current == Environment.DEVELOPMENT) {
            simple startDelay: 60 * 1000, repeatInterval: 5 * 1000
        }
        if (Environment.current == Environment.PRODUCTION) {
            simple startDelay: 60 * 1000, repeatInterval: 1 * 60 * 1000
        }
    }

    def irclogLogAppender

    def execute() {
        log.info "Begin persistent queued irclog..."
        try {
            log.debug "HOGE: " + irclogLogAppender?.queue?.size()
            //summaryUpdateService.updateAllSummary()
        } finally {
            log.info "End persistent queued irclog."
        }
    }
}
