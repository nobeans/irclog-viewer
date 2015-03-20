package irclog

import grails.util.Environment

class UpdateTodaySummaryJob {

    def concurrent = false

    static triggers = {
        if (Environment.current == Environment.DEVELOPMENT) {
            simple startDelay: 60 * 1000, repeatInterval: 1 * 60 * 1000
        }
        if (Environment.current == Environment.PRODUCTION) {
            cron startDelay: 60 * 1000, cronExpression: "0 * * * * ?"
        }
    }

    def summaryUpdateService

    def execute() {
        log.info "Begin updating today's summary..."
        try {
            summaryUpdateService.updateTodaySummary()
        } finally {
            log.info "End updating today's summary."
        }
    }
}
