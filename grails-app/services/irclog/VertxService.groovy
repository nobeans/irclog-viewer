package irclog

import irclog.vertx.DetailPushServer

class VertxService {

    DetailPushServer detailPushServer

    def start() {
        detailPushServer.start()
    }

    def stop() {
        detailPushServer.stop()
    }
}
