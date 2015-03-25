package irclog.vertx

import grails.converters.JSON
import irclog.ChannelService
import org.vertx.groovy.core.Vertx
import org.vertx.groovy.core.http.HttpServer
import org.vertx.groovy.core.http.WebSocket
import groovy.util.logging.Slf4j

@Slf4j
class TopicPushServer {

    private static final int SERVER_PORT = 8897

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
            if (!ws.path.startsWith("/irclog/topic/")) {
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
            def handlers = [:]
            allowedChannels.each { String channelName ->
                String eventBusKey = "irclog/${channelName}"
                def handler = createEventBusHandler(ws)
                vertx.eventBus.registerHandler(eventBusKey, handler)
                log.debug "Registered eventbus handler: ${eventBusKey}"
                handlers[eventBusKey] = handler
            }

            // When socket closed, dataHandler must be unregistered. Otherwise errors cause.
            ws.closeHandler {
                log.debug "WebSocket closed"
                handlers.each { String eventBusKey, handler ->
                    vertx.eventBus.unregisterHandler(eventBusKey, handler)
                    log.debug "Unregistered eventbus handler: ${eventBusKey}"
                }
            }
        }
    }

    private createEventBusHandler(WebSocket ws) {
        return { message ->
            log.debug "A handler ${inspect()} works on ${Thread.currentThread().name}"
            def body = message.body()
            if (!(body instanceof Map)) {
                log.warn "Just ignored unexpected message body: ${body}"
                return
            }
            if (body.type != "TOPIC") return
            ws.writeTextFrame((body as JSON).toString())
        }
    }

    private parseParams(ws) {
        def tokens = ws.path.replaceFirst('^/irclog/topic/', '').split("/")
        if (tokens.size() >= 1) {
            return [token: tokens[0]]
        }
        return [:]
    }

    private Collection<String> getAllowedChannels(token) {
        def tokens = vertx.sharedData.getMap("irclog.topic.push.tokens")
        log.debug "Registering tokens: ${tokens}"
        tokens.remove(token)?.split(":") ?: []
    }

    def stop() {
        httpServer.close()
        log.debug "Closed port ${SERVER_PORT}."
    }
}
