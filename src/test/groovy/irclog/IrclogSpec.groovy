package irclog

import grails.test.mixin.TestFor
import irclog.test.ConstraintUnitSpec
import irclog.utils.DomainUtils
import spock.lang.Unroll

@TestFor(Irclog)
class IrclogSpec extends ConstraintUnitSpec {

    Irclog irclog

    def setup() {
        irclog = DomainUtils.createIrclog()
    }

    def "validate: DomainUtils' default values are all valid"() {
        expect:
        irclog.validate()
    }

    @Unroll
    def "validate: #field is #error when value is '#value'"() {
        given:
        irclog[field] = value

        expect:
        validateConstraints(irclog, field, error)

        where:
        field         | error        | value
        'time'        | 'nullable'   | null
        'time'        | 'valid'      | new Date()
        'type'        | 'nullable'   | null
        'type'        | 'valid'      | 'JOIN'
        'type'        | 'not.inList' | 'UNDEFINED_TYPE'
        'message'     | 'nullable'   | null
        'message'     | 'valid'      | ''
        'message'     | 'valid'      | 'Hello'
        'nick'        | 'nullable'   | null
        'nick'        | 'blank'      | ''
        'nick'        | 'valid'      | 'MY_NICK'
        'channelName' | 'nullable'   | null
        'channelName' | 'blank'      | ''
        'channelName' | 'valid'      | '#CHANNEL'
        'channel'     | 'valid'      | null
        'channel'     | 'valid'      | DomainUtils.createChannel()
    }

    def "validate: permaId is automatically updated"() {
        given:
        irclog = DomainUtils.createIrclog(
            time: time,
            channelName: channelName,
            nick: nick,
            type: type,
            message: message
        )

        and:
        assert irclog.permaId == null

        expect:
        irclog.save(flush: true)

        and:
        irclog.permaId == permaId

        where:
        time           | channelName | nick           | type      | message      | permaId
        new Date(0)    | "#test"     | "IrclogSpec"   | "PRIVMSG" | "PERMA-ID"   | "dc44bfbdaf74caf1a2648bd56a74c930"
        new Date(0)    | "#test"     | "IrclogSpec"   | "PRIVMSG" | "PERMA-ID-2" | "0c9c3c9b3d5c293c9e5156487c1a94cd"
        new Date(0)    | "#test"     | "IrclogSpec"   | "NOTICE"  | "PERMA-ID"   | "85a7062ff2f7ae1afbe5e73034780b66"
        new Date(0)    | "#test"     | "IrclogSpec-2" | "PRIVMSG" | "PERMA-ID"   | "bc317db0f73fdb0d8e17e83450effbb8"
        new Date(0)    | "#test-2"   | "IrclogSpec"   | "PRIVMSG" | "PERMA-ID"   | "9afb26f71357e8ac26c08fd198c58af0"
        new Date(1000) | "#test"     | "IrclogSpec"   | "PRIVMSG" | "PERMA-ID"   | "077b6ed5bd67822b621bfdaf5df2a346"
    }

    def "validate: permaId is unique"() {
        given:
        def baseProps = [
            time: new Date(0),
            channelName: "#test",
            nick: "IrclogSpec",
            type: "PRIVMSG",
            message: "EXISTED"
        ]
        DomainUtils.createIrclog(baseProps).save(failOnError: true, flush: true)

        and:
        def irclog = DomainUtils.createIrclog(baseProps)

        when:
        irclog.save(flush: true)

        then:
        irclog.errors['permaId']?.code == "unique"
    }
}
