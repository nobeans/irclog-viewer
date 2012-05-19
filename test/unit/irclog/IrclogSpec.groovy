package irclog

import grails.test.mixin.TestFor
import irclog.test.ConstraintUnitSpec
import irclog.utils.DomainUtils
import spock.lang.Unroll

import static irclog.utils.DomainUtils.*

@TestFor(Irclog)
class IrclogSpec extends ConstraintUnitSpec {

    def setup() {
        mockForConstraintsTests(Irclog, [
            createIrclog(permaId: 'EXISTS_PERMAID')
        ])
    }

    def "validate: DomainUtils' default values are all valid"() {
        given:
        Irclog irclog = DomainUtils.createIrclog()

        expect:
        irclog.validate()
    }

    @Unroll
    def "validate: #field is #error when value is '#value'"() {
        given:
        Irclog irclog = DomainUtils.createIrclog(("$field" as String): value)

        expect:
        validateConstraints(irclog, field, error)

        where:
        field         | error      | value
        'time'        | 'nullable' | null
        'time'        | 'valid'    | new Date()
        'type'        | 'nullable' | null
        'type'        | 'valid'    | 'JOIN'
        'type'        | 'inList'   | 'UNDEFINED_TYPE'
        'message'     | 'nullable' | null
        'message'     | 'valid'    | ''
        'message'     | 'valid'    | 'Hello'
        'nick'        | 'nullable' | null
        'nick'        | 'blank'    | ''
        'nick'        | 'valid'    | 'MY_NICK'
        'permaId'     | 'nullable' | null
        'permaId'     | 'unique'   | 'EXISTS_PERMAID'
        'permaId'     | 'valid'    | 'UNIQUE_PERMID'
        'channelName' | 'nullable' | null
        'channelName' | 'blank'    | ''
        'channelName' | 'valid'    | '#CHANNEL'
        'channel'     | 'valid'    | null
        'channel'     | 'valid'    | DomainUtils.createChannel()
    }
}
