package irclog

import irclog.vertx.IrclogPushServer

class VertxService {

    IrclogPushServer irclogPushServer

    def start() {
        irclogPushServer.start()
    }

    def stop() {
        irclogPushServer.stop()
    }
}
