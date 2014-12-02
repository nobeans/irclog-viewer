package irclog.search

import grails.plugin.springsecurity.SpringSecurityService
import grails.validation.Validateable
import irclog.Channel
import irclog.ChannelService
import irclog.IrclogSearchService

@Validateable
class DetailQuery {

    final String period = 'oneday'
    String channel
    String type
    String date

    transient IrclogSearchService irclogSearchService
    transient ChannelService channelService
    transient SpringSecurityService springSecurityService

    def beforeValidate() {
        // Resolving default values
        channel = normalizeChannelName(channel)
        date = normalizeDate(date)
        type = type ?: 'all'
    }

    def getIrclogList() {
        def criteriaMap = toMap()
        return irclogSearchService.search(currentUser, criteriaMap, [:], 'asc').list
    }

    def getRelatedDateList() {
        return channelService.getRelatedDates(
            date,
            Channel.findByName(channel)
        ).collectEntries { String key, Date date ->
            [key, date?.format('yyyy-MM-dd')]
        }
    }

    def getChannelCandidates() {
        channelService.getAccessibleChannelList(currentUser, [:]).grep { !it.isArchived }.collect { it.name }
    }

    Map toMap() {
        def map = [
            period: period,
            channel: channel,
            type: type,
            periodOnedayDate: date,
        ]
        map.remove('')
        return map
    }

    private getCurrentUser() {
        springSecurityService.currentUser
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
