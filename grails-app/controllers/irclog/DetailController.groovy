package irclog

import grails.converters.JSON
import grails.plugins.springsecurity.SpringSecurityService
import irclog.search.DetailQuery
import org.vertx.groovy.core.Vertx

import java.security.MessageDigest

class DetailController {

    private MessageDigest digest = MessageDigest.getInstance('md5')

    Vertx vertx
    ChannelService channelService
    SpringSecurityService springSecurityService

    def redirectToLatestUrl() {
        render view: "redirectV02"
    }

    def index(DetailQuery query) {
        String token = generateOneTimeToken()
        saveTokenToVertx(token)
        return [
            token: token,
            criterion: query.toMap(),
            essentialTypes: Irclog.ESSENTIAL_TYPES,
        ]
    }

    private String saveTokenToVertx(token) {
        def person = springSecurityService.currentUser
        def channelNames = channelService.getAccessibleChannelList(person, [:])*.name
        vertx.sharedData.getMap("irclog.detail.push.tokens").put(token, channelNames.join(":"))
    }

    private String generateOneTimeToken() {
        def tokenSource = "irclog:${session.id}:${System.currentTimeMillis()}"
        digest.digest(tokenSource.bytes).collect { String.format('%x', it) }.join()
    }

    def relatedDateList(DetailQuery query) {
        render query.relatedDateList as JSON
    }

    def channelList(DetailQuery query) {
        render query.channelCandidates as JSON
    }

    def irclogList(DetailQuery query) {
        render query.irclogList.collect { Irclog irclog ->
            irclog.properties["type", "message", "nick", "permaId", "channelName"] + [time: irclog.time.format('HH:mm:ss')]
        } as JSON
    }
}
