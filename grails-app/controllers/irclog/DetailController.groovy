package irclog

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.validation.Validateable
import org.vertx.groovy.core.Vertx
import java.security.MessageDigest

class DetailController {

    private MessageDigest digest = MessageDigest.getInstance('md5')

    Vertx vertx
    ChannelService channelService
    SpringSecurityService springSecurityService
    IrclogSearchService irclogSearchService

    def index(DetailCommand command) {
        String token = generateOneTimeToken()
        saveTokenToVertx(token)
        return [
            token: token,
            criterion: command.toMap(),
            essentialTypes: Irclog.ESSENTIAL_TYPES,
        ]
    }

    private String saveTokenToVertx(token) {
        def channelNames = channelService.getAccessibleChannelList(springSecurityService.currentUser, [:])*.name
        vertx.sharedData.getMap("irclog.detail.push.tokens").put(token, channelNames.join(":"))
    }

    private String generateOneTimeToken() {
        def tokenSource = "irclog:${session.id}:${System.currentTimeMillis()}"
        digest.digest(tokenSource.bytes).collect { String.format('%x', it) }.join()
    }

    def relatedDateList(DetailCommand command) {
        def relatedDateList = channelService.getRelatedDates(command.date, Channel.findByName(command.channel)).collectEntries { String key, Date date ->
            [key, date?.format('yyyy-MM-dd')]
        }
        render relatedDateList as JSON
    }

    def channelList(DetailCommand command) {
        def channelCandidates = channelService.getAccessibleChannelList(springSecurityService.currentUser, [sort: 'name', order: 'asc']).grep { !it.isArchived || it.name == command.channel }*.name
        render channelCandidates as JSON
    }

    def irclogList(DetailCommand command) {
        def criteriaMap = command.toMap()
        def irclogList = irclogSearchService.search(springSecurityService.currentUser, criteriaMap, [:], 'asc').list.collect { Irclog irclog ->
            irclog.properties["type", "message", "nick", "permaId", "channelName"] + [time: irclog.time.format('HH:mm:ss')]
        }
        render irclogList as JSON
    }
}


@Validateable
class DetailCommand {

    String channel
    String date
    String type

    static constraints = {
        channel blank: false, nullable: false
        date blank: false, nullable: false
        type()
    }

    def beforeValidate() {
        // Resolving default values
        channel = normalizeChannelName(channel)
        date = normalizeDate(date)
        type = type ?: 'all'
    }

    Map toMap() {
        def map = [
            period: 'oneday',
            channel: channel,
            type: type,
            periodOnedayDate: date,
        ]
        map.remove('')
        return map
    }

    private static normalizeChannelName(channelName) {
        if (channelName == null) return null
        channelName.startsWith('#') ? channelName : '#' + channelName
    }

    private static normalizeDate(date) { // YYYYMMDD -> YYYY-MM-DD
        if (date == null) return null
        if (date =~ /\d{8}/) {
            return date.replaceAll(/(\d{4})(\d{2})(\d{2})/, "\$1-\$2-\$3")
        }
        return date
    }
}
