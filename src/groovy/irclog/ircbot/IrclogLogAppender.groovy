package irclog.ircbot

import groovy.util.logging.Commons
import irclog.Channel
import irclog.Irclog
import irclog.utils.DateUtils
import org.jggug.kobo.gircbot.reactors.LogAppender

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
            time: DateUtils.today,
        ]
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

}
