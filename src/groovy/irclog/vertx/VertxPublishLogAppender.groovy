package irclog.vertx

import grails.util.Holders
import irclog.utils.DateUtils
import org.jggug.kobo.gircbot.reactors.LogAppender
import org.vertx.groovy.core.Vertx

class VertxPublishLogAppender implements LogAppender {

    Vertx vertx

    @Override
    void append(String type, String channelName, String nick, String message) {
        // It's difficult to prepare a Vertx instance while Config.groovy is evaluating.
        // So it's initialized lazily.
        if (!vertx) {
            vertx = Holders.applicationContext.vertx
        }

        // "time" should be fixed here.
        def params = [type: type, channelName: channelName, nick: nick, message: message, time: DateUtils.today]

        def eb = vertx.eventBus
        eb.publish("irclog/all", params)
        eb.publish("irclog/${channelName}", params)
    }
}
