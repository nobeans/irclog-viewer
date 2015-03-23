package irclog

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import irclog.test.ConstraintUnitSpec
import irclog.utils.DomainUtils
import spock.lang.Unroll

@TestFor(Summary)
@Mock(Channel)
class SummarySpec extends ConstraintUnitSpec {

    def "validate: DomainUtils' default values are all valid"() {
        given:
        Summary summary = new Summary()
        summary.channel = DomainUtils.createChannel()

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
}
