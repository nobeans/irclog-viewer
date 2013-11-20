package irclog.vertx
import irclog.Channel
import irclog.Irclog
import org.vertx.groovy.core.Vertx

class IrclogPersister {

    private static final String EVENTBUS_KEY = "irclog/all"

    Vertx vertx
    String defaultChannelName

    private handler = { message ->
        def body = message.body()
        if (!(body instanceof Map)) {
            log.warn "Just ignored unexpected message body: ${body}"
            return
        }
        saveIrclog(body)
    }

    def start() {
        vertx.eventBus.registerHandler(EVENTBUS_KEY, handler)
    }

    def stop() {
        vertx.eventBus.unregisterHandler(EVENTBUS_KEY, handler)
    }

    private void saveIrclog(Map params) {
        Irclog.withNewTransaction {
            appendForEachTx(params)
        }
    }

    private void appendForEachTx(Map params) {
        if (!params.keySet().containsAll("type", "channelName", "nick", "message", "time")) {
            log.warn "Just ignored invalid params for irclog: ${params}"
            return
        }

        params.channelName = params.channelName ?: defaultChannelName
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
