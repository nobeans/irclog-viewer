class TopController extends Base {

    def index = {
        flash.message = flash.message // メッセージがあった場合は引き継ぐ
        //redirect(url:'/irclog/viewer')
        redirect(uri: config.irclog.viewer.defaultTargetUrl)
    }
}
