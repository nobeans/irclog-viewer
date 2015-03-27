package irclog.vertx

import grails.test.mixin.integration.Integration
import irclog.Channel
import irclog.Irclog
import irclog.utils.DateUtils
import irclog.utils.DomainUtils
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

@Integration
@Transactional
class VertxPublishLogAppenderSpec extends Specification {

    // VertxPublishLogAppender works on a new transaction and it has been committed.
    // So in this test, a transaction needs be committed to interact
    // with a transaction on VertxPublishLogAppender.

    VertxPublishLogAppender appender

    def existedChannel

    def setup() {
        appender = new VertxPublishLogAppender(defaultChannelName: "#default")
        setupChannel()
        assert Irclog.count() == 0
    }

    private void setupChannel() {
        Channel.withNewTransaction {
            existedChannel = DomainUtils.createChannel(name: "#existed").save(failOnError: true, validate: false)
        }
    }

    def cleanup() {
        Irclog.withNewTransaction {
            Irclog.list()*.delete()
            Channel.list()*.delete()
        }
    }

    def "append a irclog to unregistered channel"() {
        when:
        appender.saveIrclog(type: "PRIVMSG", channelName: "#test", nick: "user-nick", message: "Hello, world!", time: DateUtils.today)

        then:
        Irclog.count() == 1

        and:
        def irclog = Irclog.list()[0]
        irclog.type == "PRIVMSG"
        irclog.channelName == "#test"
        irclog.channel == null
        irclog.message == "Hello, world!"
        irclog.time instanceof Date
        irclog.permaId ==~ /[a-z0-9]{32}/
    }

    def "append a irclog to registered channel"() {
        when:
        appender.saveIrclog(type: "PRIVMSG", channelName: "#existed", nick: "user-nick", message: "Hello, world!", time: DateUtils.today)

        then:
        Irclog.count() == 1

        and:
        def irclog = Irclog.list()[0]
        irclog.type == "PRIVMSG"
        irclog.channelName == "#existed"
        irclog.channel == existedChannel
        irclog.message == "Hello, world!"
        irclog.time instanceof Date
        irclog.permaId ==~ /[a-z0-9]{32}/
    }

    def "append a irclog without channel name as global command with unregistered default channel"() {
        given:
        appender.defaultChannelName = "#default"

        when:
        appender.saveIrclog(type: "QUIT", channelName: null, nick: "user-nick", message: "user-nick quited from server", time: DateUtils.today)

        then:
        Irclog.count() == 1

        and:
        def irclog = Irclog.list()[0]
        irclog.type == "QUIT"
        irclog.channelName == "#default"
        irclog.channel == null
        irclog.message == "user-nick quited from server"
        irclog.time instanceof Date
        irclog.permaId ==~ /[a-z0-9]{32}/
    }

    def "append a irclog without channel name as global command with registered default channel"() {
        given:
        appender.defaultChannelName = "#existed"

        when:
        appender.saveIrclog(type: "QUIT", channelName: null, nick: "user-nick", message: "user-nick quited from server", time: DateUtils.today)

        then:
        Irclog.count() == 1

        and:
        def irclog = Irclog.list()[0]
        irclog.type == "QUIT"
        irclog.channelName == "#existed"
        irclog.channel == existedChannel
        irclog.message == "user-nick quited from server"
        irclog.time instanceof Date
        irclog.permaId ==~ /[a-z0-9]{32}/
    }

    def "append many irclog"() {
        when:
        100.times {
            appender.saveIrclog(type: "PRIVMSG", channelName: "#test", nick: "user-nick", message: "Hello, world! ${it}", time: DateUtils.today)
        }

        then: "not to violate unique constraint of permaId"
        Irclog.count() == 100
    }
}
