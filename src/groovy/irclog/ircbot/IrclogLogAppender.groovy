package irclog.ircbot

import groovy.util.logging.Commons
import irclog.Channel
import irclog.Irclog
import org.jggug.kobo.gircbot.reactors.LogAppender

import java.security.MessageDigest

@Commons
class IrclogLogAppender implements LogAppender {

    String defaultChannelName

    void append(String type, String channelName, String nick, String message) {
        Irclog.withNewTransaction {
            appendForEachTx(type, channelName, nick, message)
        }
    }

    private void appendForEachTx(String type, String channelName, String nick, String message) {
        def params = [
            type: type,
            channelName: channelName ?: defaultChannelName,
            nick: nick,
            message: message,
            time: new Date(),
        ]
        params.permaId = createPermaId(params)
        params.channel = Channel.findByName(params.channelName) // nullable

        try {
            def irclog = new Irclog(params)
            irclog.save(failOnError: true)
            log.debug "Appended: $irclog"

        } catch (e) {
            log.error("Failed to insert into database", e)

            // For manually recovering.
            log.error(params)
        }
    }

    private static createPermaId(params) {
        def base = "${params.time},${params.channelName},${params.nick},${params.type},${params.message}"
        def permaId = MessageDigest.getInstance("MD5").digest(base.getBytes("UTF-8")).collect { String.format("%02x", it & 0xff) }.join()
        assert permaId.size() == 32
        return permaId
    }
}
