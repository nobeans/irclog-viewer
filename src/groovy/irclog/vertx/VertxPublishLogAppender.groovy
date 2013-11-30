package irclog.vertx

import grails.util.Holders
import irclog.Channel
import irclog.Irclog
import irclog.utils.DateUtils
import org.jggug.kobo.gircbot.reactors.LogAppender
import org.vertx.groovy.core.Vertx

class VertxPublishLogAppender implements LogAppender {

    Vertx vertx
    String defaultChannelName

    @Override
    void append(String type, String channelName, String nick, String message) {
        setupFields()

        // Save
        Irclog irclog = Irclog.withSession {
            saveIrclog(type: type, channelName: channelName, nick: nick, message: message, time: DateUtils.today)
        }

        // For Push
        publishForPushing(irclog)
    }

    private Vertx setupFields() {
        // It's difficult to prepare a Vertx instance while Config.groovy is evaluating.
        // So it's initialized lazily.
        if (!vertx) {
            vertx = Holders.applicationContext.vertx
        }

        // It's difficult to prepare a Vertx instance while Config.groovy is evaluating.
        // So it's initialized lazily.
        if (!defaultChannelName) {
            defaultChannelName = Holders.config.irclog.ircbot.channel.asDefault
        }
    }

    private Irclog saveIrclog(Map params) {
        Irclog.withNewTransaction {
            appendForEachTx(params)
        }
    }

    private Irclog appendForEachTx(Map params) {
        if (!params.keySet().containsAll("type", "channelName", "nick", "message", "time")) {
            log.warn "Just ignored invalid params for irclog: ${params}"
            return null
        }

        // If date is converted to Long by serializing, so it should be restored.
        if (params.time instanceof Long) params.time = new Date(params.time)

        params.channelName = params.channelName ?: defaultChannelName
        params.channel = Channel.findByName(params.channelName) // nullable
        try {
            def irclog = new Irclog(params)
            irclog.save(failOnError: true)
            log.debug "Appended: $irclog"
            return irclog
        } catch (e) {
            log.error("Failed to insert into database", e)

            // For manually recovering.
            log.error(params)
        }
    }

    private publishForPushing(Irclog irclog) {
        if (!irclog) return
        def params = [
            type: irclog.type,
            channelName: irclog.channelName,
            nick: irclog.nick,
            message: irclog.message,
            permaId: irclog.permaId,
            time: irclog.time,
            formattedDate: irclog.time.format("yyyy-MM-dd"),
            formattedTime: irclog.time.format("HH:mm:ss"),
        ]
        vertx.eventBus.publish("irclog/${irclog.channelName}", params)
    }
}
