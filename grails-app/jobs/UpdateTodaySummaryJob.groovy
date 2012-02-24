package irclog

class UpdateTodaySummaryJob {

    static triggers = {
        cron name: 'updateTodaySummary', cronExpression: "0 */1 * * * ?"
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
