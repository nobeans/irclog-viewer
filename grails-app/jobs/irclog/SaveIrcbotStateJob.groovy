package irclog

import grails.util.Environment
import irclog.ircbot.Ircbot

class SaveIrcbotStateJob {

    def concurrent = false

    static triggers = {
        if (Environment.current == Environment.DEVELOPMENT) {
            simple startDelay: 30 * 1000, repeatInterval: 1 * 60 * 1000
        }
        if (Environment.current == Environment.PRODUCTION) {
            cron startDelay: 60 * 1000, cronExpression: "0 0 1 * * ?"
        }
    }

    Ircbot ircbot

    def execute() {
        log.info "Beging saving joining channels..."
        try {
            ircbot.saveState()
        } finally {
            log.info "End saving joining channels."
        }
    }
}
