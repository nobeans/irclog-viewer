package irclog

import grails.plugin.spock.IntegrationSpec
import irclog.utils.DateUtils
import irclog.utils.DomainUtils

class SummaryUpdateServiceSpec extends IntegrationSpec {

    static OTHER_COUNTS = ['yesterday', 'twoDaysAgo', 'threeDaysAgo', 'fourDaysAgo', 'fiveDaysAgo', 'sixDaysAgo', 'totalBeforeYesterday', 'total']

    SummaryUpdateService summaryUpdateService

    def ch1, ch2, ch3, ch4

    def setup() {
        setupChannel()
        setupIrclog()
    }

    def "updateTodaySummary() updates only today's count"() {
        given:
        assert Summary.list().every { summary ->
            summary.today == 0
        }

        when:
        summaryUpdateService.updateTodaySummary()

        then: "ESSENTIAL_TYPES are counted"
        ch1.summary.today == Irclog.ESSENTIAL_TYPES.size()

        and: "OPTION_TYPES are not counted"
        ch2.summary.today == 0

        and: "only ESSENTIAL_TYPES are counted"
        ch3.summary.today == Irclog.ESSENTIAL_TYPES.size()

        and: "empty channel is zero"
        ch4.summary.today == 0

        and: "other counts are zero"
        [ch1, ch2, ch3, ch4].every { ch ->
            OTHER_COUNTS.every { prop -> ch.summary[prop] == 0 }
        }
    }

    def "updateAllSummary() updates all count"() {
        given:
        assert Summary.list().every { summary ->
            summary.today == 0
        }
        assert [ch1, ch2, ch3, ch4].every { ch ->
            OTHER_COUNTS.every { prop -> ch.summary[prop] == 0 }
        }

        when:
        summaryUpdateService.updateAllSummary()

        then: "ESSENTIAL_TYPES are counted"
        ch1.summary.today == Irclog.ESSENTIAL_TYPES.size()

        and: "OPTION_TYPES are not counted"
        ch2.summary.today == 0

        and: "only ESSENTIAL_TYPES are counted"
        ch3.summary.today == Irclog.ESSENTIAL_TYPES.size()

        and: "empty channel is zero"
        ch4.summary.today == 0

        and: "other counts are zero"
        [ch1, ch2, ch3, ch4].every { ch ->
            OTHER_COUNTS.every { prop -> ch.summary[prop] == 0 }
        }
    }

    // -------------------------------------
    // Test helpers

    private setupChannel() {
        (1..4).each { num ->
            this."ch${num}" = DomainUtils.createChannel(name: "#ch${num}", description: "${10 - num}").saveWithSummary(failOnError: true)
        }
    }

    private setupIrclog() {
        def today = DateUtils.today
        def times = (0..7).collect { today - it }
        times.each { time ->
            Irclog.ESSENTIAL_TYPES.each { type ->
                saveIrclog(ch1, [type: type, time: time])
            }
            Irclog.OPTION_TYPES.each { type ->
                saveIrclog(ch2, [type: type, time: time])
            }
            Irclog.ALL_TYPES.each { type ->
                saveIrclog(ch3, [type: type, time: time])
            }
        }
    }

    private saveIrclog(ch, propMap = [:]) {
        if (propMap.time in String) {
            propMap.time = DateUtils.toDate(propMap.time)
        }
        if (propMap.channel) {
            propMap.channelName = propMap.channel.name
        }
        def defaultMap = [
            channel: ch,
            channelName: ch.name,
            time: DateUtils.today,
            nick: "user1",
            type: "PRIVMSG",
        ]
        def mergedMap = defaultMap + propMap
        def permaId = mergedMap.toString() // to avoid stack overflow
        mergedMap.permaId = permaId
        return DomainUtils.createIrclog(mergedMap).save(failOnError: true)
    }
}
