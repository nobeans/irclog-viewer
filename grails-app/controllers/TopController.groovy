class TopController {

    def index = {
        flash.message = flash.message // メッセージがあった場合は引き継ぐ
        redirect(controller:'mixedViewer')
    }
}
