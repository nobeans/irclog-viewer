package irclog.ircbot

import grails.plugin.spock.IntegrationSpec
import irclog.Channel
import irclog.Irclog
import irclog.utils.DomainUtils

class IrclogLogAppenderSpec extends IntegrationSpec {

    // IrclogLogAppender works on a new transaction and it has been committed.
    // So in this test, a transaction needs be committed to interact
    // with a transaction on IrclogLogAppender.

    IrclogLogAppender irclogLogAppender
    def existedChannel

    def setup() {
        setupChannel()
        assert Irclog.count() == 0
    }

    private void setupChannel() {
        Channel.withNewTransaction {
            existedChannel = DomainUtils.createChannel(name: "#existed").save(failOnError: true)
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
        irclogLogAppender.append("PRIVMSG", "#test", "user-nick", "Hello, world!")

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
        irclogLogAppender.append("PRIVMSG", "#existed", "user-nick", "Hello, world!")

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
        irclogLogAppender.defaultChannelName = "#default"

        when:
        irclogLogAppender.append("QUIT", null, "user-nick", "user-nick quited from server")

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
        irclogLogAppender.defaultChannelName = "#existed"

        when:
        irclogLogAppender.append("QUIT", null, "user-nick", "user-nick quited from server")

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
            irclogLogAppender.append("PRIVMSG", "#test", "user-nick", "Hello, world! ${it}")
        }

        then: "not to violate unique constraint of permaId"
        Irclog.count() == 100
    }
}
