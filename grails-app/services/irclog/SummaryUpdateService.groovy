package irclog

import irclog.utils.DateUtils

class SummaryUpdateService {

    static transactional = true

    void updateTodaySummary() {
        def yesterdayMidnight = DateUtils.today.clearTime()
        def todayMidnight = yesterdayMidnight + 1

        updateSpecifiedSummary(column: 'today', from: yesterdayMidnight, to: todayMidnight)
        updateLatestIrclog()
    }

    void updateAllSummary() {
        def yesterdayMidnight = DateUtils.today.clearTime()
        [
            [column: 'yesterday', from: yesterdayMidnight - 1, to: yesterdayMidnight],
            [column: 'twoDaysAgo', from: yesterdayMidnight - 2, to: yesterdayMidnight - 1],
            [column: 'threeDaysAgo', from: yesterdayMidnight - 3, to: yesterdayMidnight - 2],
            [column: 'fourDaysAgo', from: yesterdayMidnight - 4, to: yesterdayMidnight - 3],
            [column: 'fiveDaysAgo', from: yesterdayMidnight - 5, to: yesterdayMidnight - 4],
            [column: 'sixDaysAgo', from: yesterdayMidnight - 6, to: yesterdayMidnight - 5],
            [column: 'totalBeforeYesterday', from: DateUtils.epoch, to: yesterdayMidnight],
        ].each {
            updateSpecifiedSummary(it)
        }
        updateTodaySummary()
        log.debug "Updated all summary"
    }

    private void updateLatestIrclog() {
        Summary.list().each { summary ->
            summary.latestIrclog = Irclog.withCriteria(uniqueResult: true) {
                maxResults 1
                eq 'channel', summary.channel
                'in' 'type', Irclog.ESSENTIAL_TYPES
                order 'time', 'desc'
            }
            summary.save()
            log.debug "Updated latest irclog of ${summary.channel.name}: ${summary.latestIrclog?.id}"
        }
    }

    private void updateSpecifiedSummary(map) {
        def countsPerChannel = Irclog.withCriteria {
            projections {
                groupProperty('channel')
                rowCount()
            }
            ge 'time', map.from
            lt 'time', map.to
            'in' 'type', Irclog.ESSENTIAL_TYPES
            isNotNull "channel"
        }.collectEntries { row ->
            [row[0], row[1]] // [channel:Channel, count:int]
        }.withDefault { 0 }

        Summary.list().each { summary ->
            int count = countsPerChannel[summary.channel]
            summary[map.column] = count
            summary.save()
            log.debug "Updated ${map.column}'s summary of ${summary.channel.name}: ${count}"
        }
    }
}
