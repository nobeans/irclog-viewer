package irclog.ircbot

import grails.plugin.spock.IntegrationSpec

class IrclogLogAppenderSpec extends IntegrationSpec {

    // IrclogLogAppender works on a new transaction and it has been committed.
    // So in this test, a transaction needs be committed to interact
    // with a transaction on IrclogLogAppender.

    IrclogLogAppender irclogLogAppender

    def setup() {
        assert irclogLogAppender.queue.size() == 0
    }

    def cleanup() {
        irclogLogAppender.queue.clear()
    }

    def "append a irclog to unregistered channel"() {
        when:
        irclogLogAppender.append("PRIVMSG", "#test", "user-nick", "Hello, world!")

        then:
        assert irclogLogAppender.queue.size() == 1

        and:
        def irclog = irclogLogAppender.queue.peek()
        irclog.type == "PRIVMSG"
        irclog.channelName == "#test"
        irclog.message == "Hello, world!"
        irclog.time instanceof Date
    }

    def "append a irclog to registered channel"() {
        when:
        irclogLogAppender.append("PRIVMSG", "#existed", "user-nick", "Hello, world!")

        then:
        assert irclogLogAppender.queue.size() == 1

        and:
        def irclog = irclogLogAppender.queue.peek()
        irclog.type == "PRIVMSG"
        irclog.channelName == "#existed"
        irclog.message == "Hello, world!"
        irclog.time instanceof Date
    }

    def "append a irclog without channel name as global command with unregistered default channel"() {
        given:
        irclogLogAppender.defaultChannelName = "#default"

        when:
        irclogLogAppender.append("QUIT", null, "user-nick", "user-nick quited from server")

        then:
        assert irclogLogAppender.queue.size() == 1

        and:
        def irclog = irclogLogAppender.queue.peek()
        irclog.type == "QUIT"
        irclog.channelName == "#default"
        irclog.message == "user-nick quited from server"
        irclog.time instanceof Date
    }

    def "append a irclog without channel name as global command with registered default channel"() {
        given:
        irclogLogAppender.defaultChannelName = "#existed"

        when:
        irclogLogAppender.append("QUIT", null, "user-nick", "user-nick quited from server")

        then:
        assert irclogLogAppender.queue.size() == 1

        and:
        def irclog = irclogLogAppender.queue.peek()
        irclog.type == "QUIT"
        irclog.channelName == "#existed"
        irclog.message == "user-nick quited from server"
        irclog.time instanceof Date
    }

    def "append many irclog"() {
        when:
        100.times {
            irclogLogAppender.append("PRIVMSG", "#test", "user-nick", "Hello, world! ${it}")
        }

        then: "not to violate unique constraint of permaId"
        assert irclogLogAppender.queue.size() == 100
    }
}
