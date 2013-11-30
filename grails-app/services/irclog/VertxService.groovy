package irclog

import irclog.vertx.DetailPushServer
import irclog.vertx.SummaryPushServer

class VertxService {

    DetailPushServer detailPushServer
    SummaryPushServer summaryPushServer

    def start() {
        detailPushServer.start()
        summaryPushServer.start()
    }

    def stop() {
        detailPushServer.stop()
        summaryPushServer.stop()
    }
}
