package irclog.vertx

import grails.converters.JSON
import irclog.Channel
import irclog.ChannelService
import org.vertx.groovy.core.Vertx
import org.vertx.groovy.core.http.HttpServer
import org.vertx.groovy.core.http.WebSocket
import groovy.util.logging.Slf4j

@Slf4j
class SummaryPushServer {

    private static final int SERVER_PORT = 8898

    Vertx vertx
    ChannelService channelService
    private HttpServer httpServer

    def start() {
        httpServer = vertx.createHttpServer()
        setupPushIrclog()
        httpServer.listen(SERVER_PORT)
        log.debug "Start listening ${SERVER_PORT}."
    }

    private void setupPushIrclog() {
        httpServer.websocketHandler { ws ->
            // Checking a path
            if (!ws.path.startsWith("/irclog/summary/")) {
                println "Unsupported path: ${ws.path}"
                ws.reject()
                return
            }

            // Parsing params
            def params = parseParams(ws)
            if (!params.token) {
                log.warn "Invalid access to Websocket: path=${ws.path}, params=${params}"
                return
            }
            log.debug "Connected: ${ws.path}: with parmas=${params}"

            // Checking token
            def allowedChannels = getAllowedChannels(params.token)
            log.debug "Allowed accessing to channels: ${allowedChannels}"
            if (!allowedChannels) {
                log.warn "Channel not allowed: ${allowedChannels}"
                return
            }

            // Pushing data from EventBus
            String eventBusKey = "irclog/summary"
            def handler = createEventBusHandler(ws, allowedChannels)
            vertx.eventBus.registerHandler(eventBusKey, handler)
            log.debug "Registered eventbus handler: ${eventBusKey}"

            // When socket closed, dataHandler must be unregistered. Otherwise errors cause.
            ws.closeHandler {
                log.debug "WebSocket closed"
                vertx.eventBus.unregisterHandler(eventBusKey, handler)
                log.debug "Unregistered eventbus handler: ${eventBusKey}"
            }
        }
    }

    private createEventBusHandler(WebSocket ws, List allowedChannels) {
        return { message ->
            log.debug "A handler ${inspect()} works on ${Thread.currentThread().name}"
            def body = message.body()
            if (!(body in ["updated/today", "updated/all"])) {
                log.warn "Just ignored unexpected message body: ${body}"
                return
            }
            Channel.withNewSession {
                log.debug "Detected updating summaries: ${body}"
                def channels = Channel.findAllByNameInList(allowedChannels)
                def summaries = channels.collect { channel ->
                    def summary = channel.summary
                    def latestIrclog = summary.latestIrclog
                    return [
                        channelId: channel.id,
                        channelName: channel.name,
                        today: summary.today,
                        yesterday: summary.yesterday,
                        twoDaysAgo: summary.twoDaysAgo,
                        threeDaysAgo: summary.threeDaysAgo,
                        fourDaysAgo: summary.fourDaysAgo,
                        fiveDaysAgo: summary.fiveDaysAgo,
                        sixDaysAgo: summary.sixDaysAgo,
                        total: summary.total,
                        latestTime: latestIrclog?.time?.time ?: 0, // long type as time
                        latestNick: latestIrclog?.nick ?: "",
                        latestMessage: latestIrclog?.message ?: "",
                    ]
                }
                ws.writeTextFrame((summaries as JSON).toString())
            }
        }
    }

    private parseParams(ws) {
        def tokens = ws.path.replaceFirst('^/irclog/summary/', '').split("/")
        if (tokens.size() >= 1) {
            return [token: tokens[0]] // normalizing channel name with hash
        }
        return [:]
    }

    private Collection<String> getAllowedChannels(token) {
        def tokens = vertx.sharedData.getMap("irclog.summary.push.tokens")
        log.debug "Registering tokens: ${tokens}"
        tokens.remove(token)?.split(":") ?: []
    }

    def stop() {
        httpServer.close()
        log.debug "Closed port ${SERVER_PORT}."
    }
}
