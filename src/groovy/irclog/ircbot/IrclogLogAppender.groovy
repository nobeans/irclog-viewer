package irclog.ircbot

import groovy.util.logging.Commons
import irclog.utils.DateUtils
import org.jggug.kobo.gircbot.reactors.LogAppender

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

@Commons
class IrclogLogAppender implements LogAppender {

    String defaultChannelName
    private static final BlockingQueue queue = new LinkedBlockingQueue()

    void append(String type, String channelName, String nick, String message) {
        appendForEachTx(type, channelName, nick, message)
    }

    private void appendForEachTx(String type, String channelName, String nick, String message) {
        def params = [
            type: type,
            channelName: channelName ?: defaultChannelName,
            nick: nick,
            message: message,
            time: DateUtils.today,
        ]
        queue.add params
        log.debug "Queued: $params"
    }

}
