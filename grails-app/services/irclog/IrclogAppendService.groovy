package irclog

import grails.util.Holders
import org.jggug.kobo.gircbot.reactors.LogAppender

import java.security.MessageDigest

class IrclogAppendService implements LogAppender {

    String defaultChannelName = Holders.config.irclog.ircbot.channel.defaultForLogging

    void append(String type, String channelName, String nick, String message) {
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
            new Irclog(params).save(failOnError: true)
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
