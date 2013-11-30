package irclog.vertx

import grails.converters.JSON
import irclog.ChannelService
import org.vertx.groovy.core.Vertx
import org.vertx.groovy.core.http.HttpServer
import org.vertx.groovy.core.http.WebSocket

class IrclogPushServer {

    private static final int webSocketPort = 8899

    Vertx vertx
    ChannelService channelService
    private HttpServer httpServer

    def start() {
        httpServer = vertx.createHttpServer()
        setupPushIrclog()
        httpServer.listen(webSocketPort)
        log.debug "Start listening ${webSocketPort}."
    }

    private void setupPushIrclog() {
        httpServer.websocketHandler { ws ->
            // Checking a path
            if (!ws.path.startsWith("/irclog/detail/")) {
                println "Unsupported path: ${ws.path}"
                ws.reject()
                return
            }

            // Parsing params
            def params = parseParams(ws)
            if (!params.channelName || !params.token) {
                log.warn "Invalid access to Websocket: path=${ws.path}, params=${params}"
                return
            }
            log.debug "Connected: ${ws.path}: with parmas=${params}"

            // Checking token
            def allowedChannels = getAllowedChannels(params.token)
            log.debug "Allowed accessing to channels: ${allowedChannels}"
            if (!allowedChannels.contains(params.channelName)) {
                log.warn "Channel not allowed: ${params.channelName}"
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
            ws.writeTextFrame((body as JSON).toString())
        }
    }

    private parseParams(ws) {
        def tokens = ws.path.replaceFirst('^/irclog/detail/', '').split("/")
        if (tokens.size() >= 2) {
            return [channelName: "#" + tokens[0], token: tokens[1]] // normalizing channel name with hash
        }
        return [:]
    }

    private Collection<String> getAllowedChannels(token) {
        def tokens = vertx.sharedData.getMap("irclog.push.tokens")
        log.debug "Registering tokens: ${tokens}"
        tokens.remove(token)?.split(":") ?: []
    }

    def stop() {
        httpServer.close()
        log.debug "Closed port ${webSocketPort}."
    }
}
