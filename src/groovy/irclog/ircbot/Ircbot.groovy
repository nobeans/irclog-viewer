package irclog.ircbot

import grails.util.Holders
import org.jggug.kobo.gircbot.builder.GircBotBuilder
import org.jggug.kobo.gircbot.reactors.Logger

class Ircbot {

    GircBotBuilder builder
    IrclogLogAppender irclogLogAppender

    void start() {
        if (disable) return

        builder = new GircBotBuilder()

        Map configMap = Holders.config.irclog.ircbot.flatten()
        configMap.reactors << new Logger(irclogLogAppender)
        builder.config(configMap)

        builder.start()
    }

    private static boolean isDisable() {
        Boolean.valueOf(System.getProperty("ircbot.disable"))
    }

    void stop() {
        builder?.stop()
    }

}
