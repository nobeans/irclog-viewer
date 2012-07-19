import grails.util.Environment
import irclog.Channel
import irclog.Role
import irclog.Person
import irclog.utils.DateUtils
import static irclog.utils.DomainUtils.*
import irclog.Irclog

class BootStrap {

    def springSecurityService

    def init = { servletContext ->
        setupRolesIfNotExists()
        setupDefaultAdminUserIfNotExists()
        setupForDevelopmentEnv()
    }

    def destroy = {
    }

    private void setupRolesIfNotExists() {
        if (!Role.findByName(Role.USER)) createRole(name: Role.USER).save(failOnError: true)
        if (!Role.findByName(Role.ADMIN)) createRole(name: Role.ADMIN).save(failOnError: true)
    }

    private void setupDefaultAdminUserIfNotExists() {
        if (!Person.findByLoginName("admin")) {
            createPerson(
                loginName: "admin",
                realName: "Administrator",
                password: "admin00",
                enabled: true,
                nicks: "",
                color: "",
                role: Role.findByName(Role.ADMIN),
            ).save(failOnError: true)
        }
    }

    private setupForDevelopmentEnv() {
        if (Environment.isDevelopmentMode()) {
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
                                time: time,
                            ).save(failOnError: true)
                        }
                    }
                }
            }
        }
    }
}
