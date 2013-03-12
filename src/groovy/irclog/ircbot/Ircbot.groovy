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
        def disabled = Holders.config.irclog.ircbot.disabled || System.getProperty("ircbot.disabled")
        if (disabled) {
            return Boolean.valueOf(disabled)
        }
        return false
    }

    void stop() {
        builder?.stop()
    }

}
