package irclog.ircbot
import grails.test.mixin.Mock
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import irclog.IrcbotState
import org.jggug.kobo.gircbot.builder.GircBotBuilder
import org.jggug.kobo.gircbot.core.GircBot
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

@TestMixin(GrailsUnitTestMixin)
@Mock(IrcbotState)
class IrcbotSpec extends Specification {

    Ircbot ircbot
    def configMap = [:]

    def setup() {
        ircbot = new Ircbot(true)

        ircbot.gircBotBuilder = Mock(GircBotBuilder)
        _ * ircbot.gircBotBuilder.config >> configMap
    }

    def "starting ircbot succeeds"() {
        when:
        ircbot.start()

        then:
        1 * ircbot.gircBotBuilder.start()
    }

    def "starting after started is ignored"() {
        given:
        ircbot.start()

        when:
        ircbot.start()

        then:
        0 * ircbot.gircBotBuilder.start()
    }

    def "stopping ircbot after started succeeds"() {
        given:
        ircbot.start()

        when:
        ircbot.stop()

        then:
        1 * ircbot.gircBotBuilder.stop()
    }

    def "stopping ircbot before started is ignored"() {
        when:
        ircbot.stop()

        then:
        0 * ircbot.gircBotBuilder.stop()
    }

    def "stopping ircbot after stopped is ignored"() {
        given:
        ircbot.start()
        ircbot.stop()

        when:
        ircbot.stop()

        then:
        0 * ircbot.gircBotBuilder.stop()
    }

    def "starting and stopping ircbot are ignored while enabled is false"() {
        given:
        ircbot = new Ircbot(false)
        ircbot.gircBotBuilder = Mock(GircBotBuilder)

        when:
        ircbot.start()
        ircbot.stop()

        then:
        0 * ircbot.gircBotBuilder.start()
        0 * ircbot.gircBotBuilder.stop()
    }

    def "save joining channels is to add a record"() {
        given:
        def bot = GroovySpy(GircBot, global: true)
        ircbot.gircBotBuilder.bot >> bot

        and: 'saveState can be used after starting'
        ircbot.start()

        when:
        ircbot.saveState()

        then:
        IrcbotState.count() == 1

        and:
        1 * bot.channels >> ['#test1', '#test2']

        and:
        IrcbotState.list()[0].channels == ["#test1", "#test2"] as Set
    }

    @Unroll
    def "#savedRows ircbot states are stored with limit #limit after calling at #callCount times"() {
        given: "setup mock"
        def bot = GroovySpy(GircBot, global: true)
        _ * bot.channels >> ['#test1', '#test2']
        ircbot.gircBotBuilder.bot >> bot

        and: 'saveState can be used after starting'
        ircbot.start()

        and:
        ircbot.limitOfSavedStates = limit

        when:
        callCount.times {
            ircbot.saveState()
        }

        then:
        IrcbotState.count() == savedRows

        and:
        IrcbotState.list()*.id == savedIds

        where:
        limit | callCount | savedRows | savedIds
        1     | 1         | 1         | [1]
        1     | 2         | 1         | [2]
        1     | 3         | 1         | [3]
        2     | 1         | 1         | [1]
        2     | 2         | 2         | [1, 2]
        2     | 3         | 2         | [2, 3]
        2     | 4         | 2         | [3, 4]
        3     | 1         | 1         | [1]
        3     | 2         | 2         | [1, 2]
        3     | 3         | 3         | [1, 2, 3]
        3     | 4         | 3         | [2, 3, 4]
        3     | 5         | 3         | [3, 4, 5]
    }

    def "LATEST_SAVED_CHANNELS of 'channel.autoJoinTo' is automatically resolved when starting"() {
        given:
        configMap['channel.autoJoinTo'] = ["#test0", Ircbot.LATEST_SAVED_CHANNELS, "#test9", "#test1"]

        and:
        new IrcbotState(channels: savedChannels).save(flush: true)

        when:
        ircbot.start()

        then:
        configMap['channel.autoJoinTo'] == expectedChannels

        where:
        savedChannels                  | expectedChannels
        []                             | ["#test0", "#test1", "#test9"]
        ["#test1", "#test2", "#test3"] | ["#test0", "#test1", "#test2", "#test3", "#test9"]
    }

    def "'channel.autoJoinTo' is nullable"() {
        given:
        configMap['channel.autoJoinTo'] = null

        and:
        new IrcbotState(channels: ["#test1"]).save(flush: true)

        when:
        ircbot.start()

        then:
        configMap['channel.autoJoinTo'] == null
    }

    def "'channel.autoJoinTo' ends up empty when no rows"() {
        given:
        configMap['channel.autoJoinTo'] = [Ircbot.LATEST_SAVED_CHANNELS]

        when:
        ircbot.start()

        then:
        configMap['channel.autoJoinTo'] == []
    }

    @Ignore
    def "'channel.autoJoinTo' is resolved to latest saved channels"() {
        given:
        configMap['channel.autoJoinTo'] = [Ircbot.LATEST_SAVED_CHANNELS]

        and:
        new IrcbotState(channels: ["#test1"]).save(flush: true)
        new IrcbotState(channels: ["#test2"]).save(flush: true)
        new IrcbotState(channels: ["#test3"]).save(flush: true)
//        assert IrcbotState.listOrderByDateCreated(order: 'desc') == []

        when:
        ircbot.start()

        then:
        configMap['channel.autoJoinTo'] == ["#test3"]
    }
}
