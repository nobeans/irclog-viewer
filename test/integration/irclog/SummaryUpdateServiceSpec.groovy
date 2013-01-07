package irclog

import grails.plugin.spock.IntegrationSpec
import irclog.utils.DateUtils
import irclog.utils.DomainUtils
import spock.lang.Shared

class SummaryUpdateServiceSpec extends IntegrationSpec {

    static OTHER_COUNTS = ['yesterday', 'twoDaysAgo', 'threeDaysAgo', 'fourDaysAgo', 'fiveDaysAgo', 'sixDaysAgo', 'totalBeforeYesterday']

    SummaryUpdateService summaryUpdateService

    @Shared
    def ch1, ch2, ch3

    def setupSpec() {
        // To insert many Irclogs is very slow and these test cases access
        // to Channel and Irclog as read-only. So the fixture is setup at setupSpec.
        setupChannel()
        setupIrclog()
    }

    def setup() {
        resetSummary()
    }

    def cleanupSpec() {
        Irclog.executeUpdate("delete from Irclog")
        Summary.executeUpdate("delete from Summary")
        Channel.executeUpdate("delete from Channel")
    }

    def "updateTodaySummary() updates only today's count"() {
        when:
        summaryUpdateService.updateTodaySummary()

        then: "ch1's today and total are updated"
        assertSummary(ch1.summary, [
            today: 3,
            yesterday: 0,
            twoDaysAgo: 0,
            threeDaysAgo: 0,
            fourDaysAgo: 0,
            fiveDaysAgo: 0,
            sixDaysAgo: 0,
            totalBeforeYesterday: 0,
            total: 3,
        ])

        and: "ch2's today and total are updated"
        assertSummary(ch2.summary, [
            today: 6,
            yesterday: 0,
            twoDaysAgo: 0,
            threeDaysAgo: 0,
            fourDaysAgo: 0,
            fiveDaysAgo: 0,
            sixDaysAgo: 0,
            totalBeforeYesterday: 0,
            total: 6,
        ])

        and: "ch3's summary is all empty because there is no irclog"
        assertSummary(ch3.summary, [
            today: 0,
            yesterday: 0,
            twoDaysAgo: 0,
            threeDaysAgo: 0,
            fourDaysAgo: 0,
            fiveDaysAgo: 0,
            sixDaysAgo: 0,
            totalBeforeYesterday: 0,
            total: 0,
        ])

        and: "latestirclog are updated"
        ch1.summary.latestIrclog != null
        ch2.summary.latestIrclog != null
        ch3.summary.latestIrclog == null
    }

    def "updateAllSummary() updates all count"() {
        when:
        summaryUpdateService.updateAllSummary()

        then: "ch1's summary is updated"
        assertSummary(ch1.summary, [
            today: 3,
            yesterday: 6,
            twoDaysAgo: 9,
            threeDaysAgo: 12,
            fourDaysAgo: 15,
            fiveDaysAgo: 18,
            sixDaysAgo: 21,
            totalBeforeYesterday: 105,
            total: 108,
        ])

        and: "ch2's summary is updated"
        assertSummary(ch2.summary, [
            today: 6,
            yesterday: 9,
            twoDaysAgo: 12,
            threeDaysAgo: 15,
            fourDaysAgo: 18,
            fiveDaysAgo: 21,
            sixDaysAgo: 24,
            totalBeforeYesterday: 126,
            total: 132,
        ])

        and: "ch3's summary is all empty because there is no irclog"
        assertSummary(ch3.summary, [
            today: 0,
            yesterday: 0,
            twoDaysAgo: 0,
            threeDaysAgo: 0,
            fourDaysAgo: 0,
            fiveDaysAgo: 0,
            sixDaysAgo: 0,
            totalBeforeYesterday: 0,
            total: 0,
        ])

        and: "latest irclog are updated"
        ch1.summary.latestIrclog != null
        ch2.summary.latestIrclog != null
        ch3.summary.latestIrclog == null
    }

    // -------------------------------------
    // Test helpers

    private void assertSummary(summary, expected) { // except latestIrclog
        def keys = ['today', 'yesterday', 'twoDaysAgo', 'threeDaysAgo', 'fourDaysAgo', 'fiveDaysAgo', 'sixDaysAgo', 'totalBeforeYesterday', 'total']
        def toMap = { target -> keys.collectEntries { key -> [key, target[key]] } }
        assert toMap(summary) == toMap(expected)
    }

    private setupChannel() {
        ch1 = DomainUtils.createChannel(name: "#ch1").saveWithSummary(failOnError: true)
        ch2 = DomainUtils.createChannel(name: "#ch2").saveWithSummary(failOnError: true)
        ch3 = DomainUtils.createChannel(name: "#ch3").saveWithSummary(failOnError: true)
    }

    private setupIrclog() {
        def saveIrclog = { ordinal, channel, dateDeltaList ->
            dateDeltaList.each { dateDelta ->
                Irclog.ALL_TYPES.each { type ->
                    (ordinal + dateDelta).times {
                        def time = DateUtils.today - dateDelta
                        DomainUtils.createIrclog(
                            channelName: channel.name,
                            channel: channel,
                            type: type,
                            time: time
                        ).save(failOnError: true)
                    }
                }
            }
        }
        saveIrclog(1, ch1, (0..7))
        saveIrclog(2, ch2, (0..7))
    }

    private resetSummary() {
        // Summary was already created when Channel was created.
        // this method resets all counts to zero and latestIrclog to null.
        Summary.list().each { summary ->
            summary.today = 0
            OTHER_COUNTS.each { prop -> summary[prop] = 0 }
            summary.total = 0
            summary.totalBeforeYesterday = 0
            summary.latestIrclog = null
        }
    }
}
