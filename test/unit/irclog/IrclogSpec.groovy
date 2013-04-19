package irclog

import grails.test.mixin.TestFor
import irclog.test.ConstraintUnitSpec
import irclog.utils.DomainUtils
import spock.lang.Unroll

@TestFor(Irclog)
class IrclogSpec extends ConstraintUnitSpec {

    Irclog irclog

    def baseIrclogParams
    def existedIrclog

    def setup() {
        baseIrclogParams = [
            time: new Date(0),
            channelName: "#test",
            nick: "IrclogSpec",
            type: "PRIVMSG",
            message: "BASE"
        ]

        existedIrclog = DomainUtils.createIrclog(baseIrclogParams + [message: "EXISTED"])
        existedIrclog.validate() // set up permaId
        mockForConstraintsTests(Irclog, [existedIrclog])

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
        'channelName' | 'nullable' | null
        'channelName' | 'blank'    | ''
        'channelName' | 'valid'    | '#CHANNEL'
        'channel'     | 'valid'    | null
        'channel'     | 'valid'    | DomainUtils.createChannel()
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
        irclog.validate()

        and:
        irclog.permaId == permaId

        where:
        time           | channelName | nick           | type      | message      | permaId
        new Date(0)    | "#test"     | "IrclogSpec"   | "PRIVMSG" | "PERMA-ID"   | "26707f7a9f3c0989fd0716ec447049dc"
        new Date(0)    | "#test"     | "IrclogSpec"   | "PRIVMSG" | "PERMA-ID-2" | "a379e161378ffd9336620e48d0a31c9e"
        new Date(0)    | "#test"     | "IrclogSpec"   | "NOTICE"  | "PERMA-ID"   | "7baebe2e58a0b1e9c3fc236c501c8def"
        new Date(0)    | "#test"     | "IrclogSpec-2" | "PRIVMSG" | "PERMA-ID"   | "6b0554df45fa58ea2f53861c0ca6f112"
        new Date(0)    | "#test-2"   | "IrclogSpec"   | "PRIVMSG" | "PERMA-ID"   | "739afd5c3bd174873c1d41275a79417b"
        new Date(1000) | "#test"     | "IrclogSpec"   | "PRIVMSG" | "PERMA-ID"   | "6ae2f62f2ebd281d912b401b3c40ad60"
    }

    def "validate: permaId is unique"() {
        given:
        irclog = DomainUtils.createIrclog(baseIrclogParams + [message: "EXISTED"])

        and:
        assert irclog.permaId == null

        expect:
        irclog.validate() == false
    }
}
