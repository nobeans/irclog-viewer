package irclog

import grails.util.Environment

class UpdateAllSummaryJob {

    def concurrent = false

    static triggers = {
        if (Environment.current == Environment.DEVELOPMENT) {
            cron name: 'updateAllSummary', cronExpression: "*/15 * * * * ?"
        } else {
            cron name: 'updateAllSummary', cronExpression: "0 0 1 * * ?"
        }
    }

    def summaryUpdateService

    def execute() {
        log.info "Begin updating all summary..."
        try {
            summaryUpdateService.updateAllSummary()
        } finally {
            log.info "End updating all summary."
        }
    }
}
