package irclog.ircbot

import irclog.IrcbotState
import org.jggug.kobo.gircbot.builder.GircBotBuilder

class Ircbot {

    static final String LATEST_SAVED_CHANNELS = 'LATEST_SAVED_CHANNELS'

    GircBotBuilder gircBotBuilder

    private final boolean enabled
    private boolean started = false
    int limitOfSavedStates = 1

    Ircbot(boolean enabled = false) {
        // enabled flag can be specified only when constructor at once.
        this.enabled = enabled
    }

    synchronized void start() {
        if (!enabled || started) return

        resolveLatestSavedChannelsIfNeeds()
        gircBotBuilder.start()
        started = true
    }

    private void resolveLatestSavedChannelsIfNeeds() {
        def channels = gircBotBuilder.config["channel.autoJoinTo"]
        if (!channels) return

        gircBotBuilder.config["channel.autoJoinTo"] = channels.collect { channel ->
            if (channel == LATEST_SAVED_CHANNELS) {
                def latestState = IrcbotState.list(sort: 'dateCreated', order: 'desc')[0]
                return latestState?.channels
            }
            return channel
        }.flatten().findAll { it }.unique().sort()
    }

    synchronized void stop() {
        if (!enabled || !started) return

        gircBotBuilder.stop()
        started = false
    }

    synchronized void saveState() {
        if (!enabled || !started) return

        // Save a current state
        def state = new IrcbotState()
        gircBotBuilder.bot.channels.each { channelName ->
            state.addToChannels channelName
        }
        state.save(flush: true)

        // Dispose old states if needs
        int countShouldDelete = IrcbotState.count() - limitOfSavedStates
        if (countShouldDelete > 0) {
            IrcbotState.listOrderByDateCreated(order: 'asc').take(countShouldDelete)*.delete(flush: true)
        }
    }
}
