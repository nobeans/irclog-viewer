package irclog

import irclog.utils.DateUtils
import org.vertx.groovy.core.Vertx

class SummaryUpdateService {

    static transactional = true

    Vertx vertx

    void updateTodaySummary() {
        def yesterdayMidnight = DateUtils.today.clearTime()
        def todayMidnight = yesterdayMidnight + 1

        updateSpecifiedSummary(column: 'today', from: yesterdayMidnight, to: todayMidnight)
        updateLatestIrclog()

        vertx.eventBus.publish("irclog/summary", "updated/today")
        log.debug "Updated today's summary"
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

        vertx.eventBus.publish("irclog/summary", "updated/all")
        log.debug "Updated all summary"
    }

    private void updateLatestIrclog() {
        Channel.list().each { channel ->
            def summary = channel.summary
            summary.latestIrclog = Irclog.withCriteria(uniqueResult: true) {
                maxResults 1
                eq 'channel', summary.channel
                'in' 'type', Irclog.ESSENTIAL_TYPES
                order 'time', 'desc'
            }
            summary.save(flush: true)
            log.debug "Updated latest irclog of ${channel.name}: ${summary.latestIrclog?.id}"
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

        Channel.list().each { channel ->
            def summary = channel.summary
            int count = countsPerChannel[channel]
            summary[map.column] = count
            summary.save(flush: true)
            log.debug "Updated ${map.column}'s summary of ${channel.name}: ${count}"
        }
    }
}
