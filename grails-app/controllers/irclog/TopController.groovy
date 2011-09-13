package irclog

class TopController {
    def index = {
        log.info "Passing through via Top"
        flash.message = flash.message // メッセージがあった場合は引き継ぐ
        redirect(uri: grailsApplication.config.irclog.viewer.defaultTargetUrl)
    }
}
