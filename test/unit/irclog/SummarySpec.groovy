package irclog

import grails.test.mixin.TestFor
import irclog.test.ConstraintUnitSpec
import irclog.utils.DomainUtils
import spock.lang.Unroll

import static irclog.utils.DomainUtils.*

@TestFor(Summary)
class SummarySpec extends ConstraintUnitSpec {

    def channel

    def setup() {
        channel = DomainUtils.createChannel(name: "EXISTED_CHANNEL")
        mockForConstraintsTests(Summary, [
            DomainUtils.createSummary(channel: channel)
        ])
    }

    def "validate: DomainUtils' default values are all valid"() {
        given:
        Summary summary = DomainUtils.createSummary()

        expect:
        summary.validate()
    }

    @Unroll
    def "validate: #field is #error when value is '#value'"() {
        given:
        Summary summary = DomainUtils.createSummary(("$field" as String): value)

        expect:
        validateConstraints(summary, field, error)

        where:
        field                  | error      | value
        'channel'              | 'nullable' | null
        'lastUpdated'          | 'valid'    | null // it's overlooked at validation because of auto-timestamping property
        'today'                | 'nullable' | null
        'yesterday'            | 'nullable' | null
        'twoDaysAgo'           | 'nullable' | null
        'threeDaysAgo'         | 'nullable' | null
        'fourDaysAgo'          | 'nullable' | null
        'fiveDaysAgo'          | 'nullable' | null
        'sixDaysAgo'           | 'nullable' | null
        'totalBeforeYesterday' | 'nullable' | null
        'latestIrclog'         | 'nullable' | null
    }

    def "validate: channel is unique in case of summary for existed channel"() {
        given:
        Summary summary = DomainUtils.createSummary(channel: channel)

        expect:
        validateConstraints(summary, 'channel', 'unique')
    }

    @Unroll
    def "total: total of today's #today and totalBeforeYesterday's #totalBeforeYesterday is #total"() {
        given:
        Summary summary = createSummary(today: today, totalBeforeYesterday: totalBeforeYesterday)

        expect:
        summary.total() == total

        where:
        today | totalBeforeYesterday | total
        1     | 2                    | 3
        0     | 2                    | 2
        1     | 0                    | 1
        1     | 8                    | 9
    }
}
