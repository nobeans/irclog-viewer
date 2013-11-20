package irclog

import irclog.vertx.IrclogPersister
import org.vertx.groovy.core.Vertx

class VertxService {

    Vertx vertx
    IrclogPersister irclogPersister

    def start() {
        irclogPersister.start()
    }

    def stop() {
        irclogPersister.stop()
    }
}
