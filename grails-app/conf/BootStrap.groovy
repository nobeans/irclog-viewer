import irclog.Irclog
import irclog.Person
import irclog.Role
import irclog.ircbot.Ircbot
import irclog.utils.DateUtils
import org.codehaus.groovy.grails.commons.GrailsApplication

import static irclog.utils.DomainUtils.*

class BootStrap {

    Ircbot ircbot

    def init = { servletContext ->
        setupRolesIfNotExists()
        setupDefaultAdminUserIfNotExists()
        environments {
            development {
                setupForDevelopmentEnv()
                ircbot.start()
            }
            production {
                ircbot.start()
            }
        }
    }

    def destroy = {
        ircbot.stop()
    }

    private static void setupRolesIfNotExists() {
        if (!Role.findByName(Role.USER)) createRole(name: Role.USER).save(failOnError: true)
        if (!Role.findByName(Role.ADMIN)) createRole(name: Role.ADMIN).save(failOnError: true)
    }

    private static void setupDefaultAdminUserIfNotExists() {
        if (!Person.findByLoginName("admin")) {
            createPerson(
                loginName: "admin",
                realName: "Administrator",
                password: "admin00",
                enabled: true,
                nicks: "",
                color: "",
                role: Role.findByName(Role.ADMIN)
            ).save(failOnError: true)
        }
    }

    private static setupForDevelopmentEnv() {
        def channels = [
            createChannel(name: "#test1", isPrivate: true),
            createChannel(name: "#test2", isPrivate: false, secretKey: ""),
            createChannel(name: "#test3", isPrivate: true),
        ].collect { it.saveWithSummary(failOnError: true) }

        (0..7).each { dateDelta ->
            channels.eachWithIndex { channel, index ->
                Irclog.ALL_TYPES.each { type ->
                    (index + dateDelta + 1).times {
                        def time = DateUtils.today - dateDelta
                        createIrclog(
                            channelName: channel.name,
                            channel: channel,
                            type: type,
                            time: time
                        ).save(failOnError: true)
                    }
                }
            }
        }
    }
}
