package irclog

class UpdateAllSummaryJob {

    def concurrent = false

    static triggers = {
        cron name: 'updateAllSummary', cronExpression: "0 0 1 * * ?"
    }

    def summaryService

    def execute() {
        log.info "Begin updating all summary..."
        try {
            summaryService.updateAllSummary()
        } finally {
            log.info "End updating all summary."
        }
    }
}
