package irclog

import grails.test.mixin.TestFor
import irclog.test.ConstraintUnitSpec
import irclog.utils.DomainUtils
import spock.lang.Unroll

@TestFor(Summary)
class SummarySpec extends ConstraintUnitSpec {

    def channel

    def setup() {
        channel = DomainUtils.createChannel(name: "EXISTED_CHANNEL")
        mockForConstraintsTests(Summary, [
            new Summary(channel: channel)
        ])
    }

    def "validate: DomainUtils' default values are all valid"() {
        given:
        Summary summary = new Summary()

        and: "except channel because summary is naturally saved via cascade from channel"
        summary.channel = new Channel()

        expect:
        summary.validate()
    }

    @Unroll
    def "validate: #field is #error when value is '#value'"() {
        given:
        Summary summary = new Summary()
        summary[field] = value

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
        'latestIrclog'         | 'valid'    | null
    }

    def "validate: channel is unique in case of summary for existed channel"() {
        given:
        Summary summary = new Summary()
        summary.channel = channel

        expect:
        validateConstraints(summary, 'channel', 'unique')
    }
}
