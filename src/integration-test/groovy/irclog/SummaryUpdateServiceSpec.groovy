package irclog

import grails.test.mixin.integration.Integration
import grails.test.runtime.DirtiesRuntime
import grails.transaction.Rollback
import irclog.utils.DateUtils
import irclog.utils.DomainUtils
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Specification
import spock.lang.Shared
import org.springframework.test.annotation.*


@Integration
@Stepwise
class SummaryUpdateServiceSpec extends Specification {

    private static final int DEFAULT_COUNT_FOR_TEST = -1
    private static final PAST_COUNT_COLUMNS = ['yesterday', 'twoDaysAgo', 'threeDaysAgo', 'fourDaysAgo', 'fiveDaysAgo', 'sixDaysAgo', 'totalBeforeYesterday']
    private static final ALL_COUNT_COLUMNS = ['today', * PAST_COUNT_COLUMNS, 'total']

    SummaryUpdateService summaryUpdateService

    @Shared
    boolean initialized = false

    def setup() {
        // To insert many Irclogs is very slow and these test cases access
        // to Channel and Irclog as read-only. So the fixture is setup at setupSpec.
        if (!initialized) {
            setupChannel()
            setupIrclog()
            initialized = true
        }
        resetAllSummary()
    }

//    def cleanupSpec() {
//        // HQL cannot be used to cascade correctly.
//        Channel.withNewSession {
//            Channel.list()*.delete(flush: true)
//            Irclog.executeUpdate("delete from Irclog")
//        }
//    }

    def "updateTodaySummary() updates only today's count"() {
        when:
        summaryUpdateService.updateTodaySummary()

        then: "ch1's today and total are updated"
        def ch1 = Channel.findByName("#ch1")
        assertSummary(ch1.summary, createExpectedSummary(
            today: 3,
            total: ch1.summary.totalBeforeYesterday + 3,
            latestIrclog: latestIrclogOf(ch1)
        ))

        and: "ch2's today's summary is empty because there is no irclog of today"
        def ch2 = Channel.findByName("#ch2")
        assertSummary(ch2.summary, createExpectedSummary(
            today: 0,
            total: ch2.summary.totalBeforeYesterday,
            latestIrclog: latestIrclogOf(ch2)
        ))

        and: "ch3's summary is all empty because there is no irclog"
        def ch3 = Channel.findByName("#ch3")
        assertSummary(ch3.summary, createExpectedSummary(
            today: 0,
            total: ch3.summary.totalBeforeYesterday,
            latestIrclog: latestIrclogOf(ch3)
        ))

        and: "latest irclog are updated"
        ch1.summary.latestIrclog != null
        ch2.summary.latestIrclog != null
        ch3.summary.latestIrclog == null
    }

    @DirtiesContext
    @DirtiesRuntime
    def "updateAllSummary() updates all count"() {
        when:
        summaryUpdateService.updateAllSummary()

        then: "ch1's summary is updated"
        def ch1 = Channel.findByName("#ch1")
        assertSummary(ch1.summary, createExpectedSummary(
            today: 3,
            yesterday: 6,
            twoDaysAgo: 9,
            threeDaysAgo: 12,
            fourDaysAgo: 15,
            fiveDaysAgo: 18,
            sixDaysAgo: 21,
            totalBeforeYesterday: 105,
            total: 108,
            latestIrclog: latestIrclogOf(ch1)
        ))

        and: "ch2's summary is updated"
        def ch2 = Channel.findByName("#ch2")
        assertSummary(ch2.summary, createExpectedSummary(
            today: 0,
            yesterday: 9,
            twoDaysAgo: 12,
            threeDaysAgo: 15,
            fourDaysAgo: 18,
            fiveDaysAgo: 21,
            sixDaysAgo: 24,
            totalBeforeYesterday: 126,
            total: 126,
            latestIrclog: latestIrclogOf(ch2)
        ))

        and: "ch3's summary is all empty because there is no irclog"
        def ch3 = Channel.findByName("#ch3")
        assertSummary(ch3.summary, createExpectedSummary(
            today: 0,
            yesterday: 0,
            twoDaysAgo: 0,
            threeDaysAgo: 0,
            fourDaysAgo: 0,
            fiveDaysAgo: 0,
            sixDaysAgo: 0,
            totalBeforeYesterday: 0,
            total: 0,
            latestIrclog: latestIrclogOf(ch3)
        ))

        and: "latest irclog are updated"
        ch1.summary.latestIrclog != null
        ch2.summary.latestIrclog != null
        ch3.summary.latestIrclog == null
    }

    // -------------------------------------
    // Test helpers

    private void assertSummary(summary, expected) {
        def toMap = { target -> ALL_COUNT_COLUMNS.collectEntries { key -> [key, target[key]] } }
        assert toMap(summary) == toMap(expected)
        assert summary.latestIrclog == expected.latestIrclog
    }

    private setupChannel() {
        // summary records corresponding each channel are also created.
        DomainUtils.createChannel(name: "#ch1").save(failOnError: true, flush: true)
        DomainUtils.createChannel(name: "#ch2").save(failOnError: true, flush: true)
        DomainUtils.createChannel(name: "#ch3").save(failOnError: true, flush: true)
    }

    private setupIrclog(channels) {
        def saveIrclog = { ordinal, channel, dateDeltaList ->
            dateDeltaList.each { dateDelta ->
                Irclog.ALL_TYPES.each { type ->
                    (ordinal + dateDelta).times {
                        def time = DateUtils.today.clearTime() - dateDelta
                        DomainUtils.createIrclog(
                            channelName: channel.name,
                            channel: channel,
                            type: type,
                            time: time
                        ).save(failOnError: true, flush: false)
                    }
                }
            }
        }
        saveIrclog(1, Channel.findByName("#ch1"), (0..7))
        saveIrclog(2, Channel.findByName("#ch2"), (1..7)) // ch2 hasn't TODAY's irclog

        Irclog.withSession { it.flush() }
    }

    private resetAllSummary() {
        // Summary was already created when Channel was created.
        // this method resets all counts to zero and latestIrclog to null.
        Channel.list().each { channel ->
            def summary = channel.summary
            summary.today = DEFAULT_COUNT_FOR_TEST
            PAST_COUNT_COLUMNS.each { prop -> summary[prop] = DEFAULT_COUNT_FOR_TEST }
            summary.totalBeforeYesterday = DEFAULT_COUNT_FOR_TEST
            summary.total = summary.today + summary.totalBeforeYesterday
            summary.latestIrclog = null
        }
    }

    private createExpectedSummary(overrideMap) {
        new Summary([
            today: DEFAULT_COUNT_FOR_TEST,
            yesterday: DEFAULT_COUNT_FOR_TEST,
            twoDaysAgo: DEFAULT_COUNT_FOR_TEST,
            threeDaysAgo: DEFAULT_COUNT_FOR_TEST,
            fourDaysAgo: DEFAULT_COUNT_FOR_TEST,
            fiveDaysAgo: DEFAULT_COUNT_FOR_TEST,
            sixDaysAgo: DEFAULT_COUNT_FOR_TEST,
            totalBeforeYesterday: DEFAULT_COUNT_FOR_TEST,
            total: DEFAULT_COUNT_FOR_TEST
        ] + overrideMap)
    }

    private latestIrclogOf(channel) {
        Irclog.withCriteria {
            eq 'channel', channel
            'in' 'type', Irclog.ESSENTIAL_TYPES
            order "time", "desc"
        }.with {
            empty ? null : first()
        }
    }
}
