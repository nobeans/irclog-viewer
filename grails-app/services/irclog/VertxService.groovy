package irclog

import irclog.vertx.DetailPushServer
import irclog.vertx.SummaryPushServer
import irclog.vertx.TopicPushServer

class VertxService {

    TopicPushServer topicPushServer
    SummaryPushServer summaryPushServer
    DetailPushServer detailPushServer

    def start() {
        topicPushServer.start()
        summaryPushServer.start()
        detailPushServer.start()
    }

    def stop() {
        topicPushServer.stop()
        summaryPushServer.stop()
        detailPushServer.stop()
    }
}
