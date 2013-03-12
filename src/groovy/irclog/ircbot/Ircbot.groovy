package irclog.ircbot

import grails.util.Holders
import org.jggug.kobo.gircbot.builder.GircBotBuilder
import org.jggug.kobo.gircbot.reactors.Logger

class Ircbot {

    GircBotBuilder builder
    IrclogLogAppender irclogLogAppender

    void start() {
        if (disabled) return

        builder = new GircBotBuilder()

        Map configMap = Holders.config.irclog.ircbot.flatten()
        configMap.reactors << new Logger(irclogLogAppender)
        builder.config(configMap)

        builder.start()
    }

    private static boolean isDisabled() {
        return Holders.config.irclog.ircbot.enable || false
    }

    void stop() {
        builder?.stop()
    }

}
