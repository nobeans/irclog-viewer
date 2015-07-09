package irclog

import grails.test.mixin.TestFor
import irclog.test.ConstraintUnitSpec
import irclog.utils.DomainUtils
import spock.lang.Unroll

@TestFor(Channel)
class ChannelSpec extends ConstraintUnitSpec {

    def "validate: DomainUtils' default values are all valid"() {
        given:
        Channel channel = DomainUtils.createChannel()

        expect:
        channel.validate()
    }

    @Unroll
    def "validate: #field is #error when value is '#value'"() {
        given:
        setupExistedChannel()

        and:
        Channel channel = DomainUtils.createChannel("$field": value)

        expect:
        validateConstraints(channel, field, error)

        where:
        field         | error              | value
        'name'        | 'nullable'         | null
        'name'        | 'blank'            | ''
        'name'        | 'unique'           | '#EXISTED_CHANNEL'
        'name'        | 'valid'            | '#' * 100
        'name'        | 'maxSize.exceeded' | '#' * 101
        'name'        | 'matches.invalid'  | '#'
        'name'        | 'matches.invalid'  | 'NO_HASH'
        'name'        | 'matches.invalid'  | '# HAS SPACE'
        'description' | 'nullable'         | null
        'isPrivate'   | 'nullable'         | null
        'isArchived'  | 'nullable'         | null
        'secretKey'   | 'valid'            | '#' * 100
        'secretKey'   | 'maxSize.exceeded' | '#' * 101
    }

    @Unroll
    def "validate: secretKey is #error when isPrivate is #isPrivate"() {
        given:
        Channel channel = DomainUtils.createChannel(
            isPrivate: isPrivate,
            secretKey: secretKey
        )

        expect:
        validateConstraints(channel, 'secretKey', error)

        where:
        isPrivate | secretKey         | error
        true      | '1234'            | 'valid'
        false     | ''                | 'valid'
        true      | ''                | 'validator.invalid'
        false     | 'SHOULD_BE_EMPTY' | 'validator.invalid'
    }

    def "two channel instances which have same name equal"() {
        given:
        def channel1 = DomainUtils.createChannel(name: name1)
        def channel2 = DomainUtils.createChannel(name: name2)

        expect:
        (channel1 == channel2) == equality

        where:
        name1    | name2    | equality
        "#test1" | "#test1" | true
        "#test2" | "#test2" | true
        "#test3" | "#test3" | true
        "#test1" | "#test2" | false
    }

    def "validate: summary is unique"() {
        given:
        def existedChannel = setupExistedChannel()

        and:
        Channel channel = DomainUtils.createChannel()
        channel.summary = existedChannel.summary

        expect:
        validateConstraints(channel, 'summary', 'unique')
    }

    private setupExistedChannel() {
        DomainUtils.createChannel(name: "#EXISTED_CHANNEL").save(failOnError: true, flush: true)
    }
}
