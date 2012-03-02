package irclog

import grails.util.Environment

class UpdateTodaySummaryJob {

    def concurrent = false

    static triggers = {
        if (Environment.current == Environment.DEVELOPMENT) {
            cron name: 'updateTodaySummary', cronExpression: "*/15 * * * * ?"
        } else {
            cron name: 'updateTodaySummary', cronExpression: "0 */1 * * * ?"
        }
    }

    def summaryService

    def execute() {
        log.info "Begin updating today's summary..."
        try {
            summaryService.updateTodaySummary()
        } finally {
            log.info "End updating today's summary."
        }
    }
}
