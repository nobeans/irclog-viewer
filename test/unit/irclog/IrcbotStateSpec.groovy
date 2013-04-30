package irclog

import grails.test.mixin.TestFor
import irclog.test.ConstraintUnitSpec
import spock.lang.Unroll

@TestFor(IrcbotState)
class IrcbotStateSpec extends ConstraintUnitSpec {

    def setup() {
        mockForConstraintsTests(IrcbotState)
    }

    @Unroll
    def "validate: #field is #error when value is '#value'"() {
        given:
        IrcbotState ircbotState = new IrcbotState(("$field" as String): value)

        expect:
        validateConstraints(ircbotState, field, error)

        where:
        field         | error   | value
        'channels'    | 'valid' | null
        'channels'    | 'valid' | ["#test1", "#test2"]
        'dateCreated' | 'valid' | null // auto-timestamping
    }
}
