package irclog

import irclog.utils.DateUtils

class SummaryUpdateService {

    static transactional = true

    void updateTodaySummary() {
        def yesterdayMidnight = DateUtils.today.clearTime()
        def todayMidnight = yesterdayMidnight + 1

        updateSpecifiedSummary(column:'today', from: yesterdayMidnight, to:todayMidnight)

        Summary.list().collect { summary ->
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

    private void updateSpecifiedSummary(map) {
        Irclog.withCriteria {
            projections {
                groupProperty('channel')
                rowCount()
            }
            between 'time', map.from, map.to
            'in' 'type', Irclog.ESSENTIAL_TYPES
        }.each { result ->
            def channel = result[0]
            def count = result[1]
            def summary = channel.summary
            summary[map.column] = count
            summary.save()
            log.debug "Updated ${map.column}'s summary of ${channel.name}: ${count}"
        }
    }
}
