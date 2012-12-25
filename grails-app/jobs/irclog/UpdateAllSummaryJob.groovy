package irclog

import grails.util.Environment

class UpdateAllSummaryJob {

    def concurrent = false

    static triggers = {
        if (Environment.current == Environment.DEVELOPMENT) {
            simple startDelay: 30 * 1000, repeatInterval: 1 * 60 * 1000
        }
        if (Environment.current == Environment.PRODUCTION) {
            cron startDelay: 60 * 1000, cronExpression: "0 0 1 * * ?"
        }
    }

    def summaryUpdateService

    def execute() {
        log.info "Begin updating all summaries..."
        try {
            summaryUpdateService.updateAllSummary()
        } finally {
            log.info "End updating all summaries."
        }
    }
}
