class TopController extends Base {

    def index = {
        log.info "Passing through via Top"
        flash.message = flash.message // メッセージがあった場合は引き継ぐ
        redirect(uri: config.irclog.viewer.defaultTargetUrl)
    }
}
