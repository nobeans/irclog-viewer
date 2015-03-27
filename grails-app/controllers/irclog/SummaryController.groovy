package irclog

import grails.converters.JSON
import org.vertx.groovy.core.Vertx

import java.security.MessageDigest

class SummaryController {

    private MessageDigest digest = MessageDigest.getInstance('md5')

    Vertx vertx
    SpringSecurityService springSecurityService
    ChannelService channelService
    TopicService topicService

    def index() {
        String topicToken = generateOneTimeToken()
        String summaryToken = generateOneTimeToken()
        saveTokenToVertx("irclog.topic.push.tokens", topicToken)
        saveTokenToVertx("irclog.summary.push.tokens", summaryToken)
        [
            topicToken: topicToken,
            summaryToken: summaryToken,
            nickPersonList: Person.list(),
        ]
    }

    def topicList() {
        render topicService.getHotTopicList(allowedChannels) as JSON
    }

    def summaryList() {
        render allowedChannels.collect { channel ->
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
        } as JSON
    }

    private ArrayList<Channel> getAllowedChannels() {
        channelService.getAccessibleChannelList(springSecurityService.currentUser, [:]).grep { !it.isArchived }
    }

    // TODO 共通化してVertxServiceへ
    private String saveTokenToVertx(key, token) {
        def person = springSecurityService.currentUser
        def channelNames = channelService.getAccessibleChannelList(person, [:])*.name
        vertx.sharedData.getMap(key).put(token, channelNames.join(":"))
    }

    private String generateOneTimeToken() {
        def tokenSource = "irclog:${session.id}:${System.currentTimeMillis()}"
        digest.digest(tokenSource.bytes).collect { String.format('%x', it) }.join()
    }
}
