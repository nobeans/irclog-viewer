package irclog

import spock.lang.Unroll
import grails.test.mixin.TestFor
import irclog.utils.DomainUtils
import irclog.test.ConstraintUnitSpec

@TestFor(Channel)
class ChannelSpec extends ConstraintUnitSpec {

    def setup() {
        mockForConstraintsTests(Channel, [
            DomainUtils.createChannel(name:"#EXISTED_CHANNEL")
        ])
    }

    def "validate: DomainUtils' default values are all valid"() {
        when:
        Channel channel = DomainUtils.createChannel()

        then:
        assert channel.validate()
    }

    @Unroll
    def "validate: #field is #error"() {
        when:
        Channel channel = DomainUtils.createChannel("$field": value)

        then:
        validateConstraints(channel, field, error)

        where:
        field            | error      | value
        'name'           | 'nullable' | null
        'name'           | 'blank'    | ''
        'name'           | 'unique'   | '#EXISTED_CHANNEL'
        'name'           | 'valid'    | '#'*100
        'name'           | 'maxSize'  | '#'*101
        'name'           | 'matches'  | 'NO_HASH'
        'description'    | 'nullable' | null
        'isPrivate'      | 'nullable' | null
        'isArchived'     | 'nullable' | null
        'secretKey'      | 'valid'    | '#'*100
        'secretKey'      | 'maxSize'  | '#'*101
    }

    @Unroll
    def "validate: secretKey is #error when isPrivate is #isPrivate"() {
        when:
        Channel channel = DomainUtils.createChannel(
            isPrivate: isPrivate,
            secretKey: secretKey
        )

        then:
        validateConstraints(channel, 'secretKey', error)

        where:
        isPrivate | secretKey           | error
        true      | '1234'              | 'valid'
        false     | ''                  | 'valid'
        true      | ''                  | 'validator'
        false     | 'SHOULD_BE_EMPTY'   | 'validator'
    }

    def "two channel instances which have same name equal"() {
        when:
        def channel1 = DomainUtils.createChannel(name:name1)
        def channel2 = DomainUtils.createChannel(name:name2)

        then:
        (channel1 == channel2) == equality

        where:
        name1    | name2    | equality
        "#test1" | "#test1" | true
        "#test2" | "#test2" | true
        "#test3" | "#test3" | true
        "#test1" | "#test2" | false
    }
}
