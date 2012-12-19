import irclog.Irclog
import irclog.IrclogAppendService
import irclog.Person
import irclog.Role
import irclog.utils.DateUtils
import org.jggug.kobo.gircbot.builder.GircBotBuilder
import org.jggug.kobo.gircbot.reactors.Logger

import static irclog.utils.DomainUtils.*

class BootStrap {

    def grailsApplication
    def irclogAppendService
    def ircbot

    def init = { servletContext ->
        setupRolesIfNotExists()
        setupDefaultAdminUserIfNotExists()
        environments {
            development {
                setupForDevelopmentEnv()
                startBot()
            }
            production {
                startBot()
            }
        }
    }

    def destroy = {
        stopBot()
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
                role: Role.findByName(Role.ADMIN)
            ).save(failOnError: true)
        }
    }

    private setupForDevelopmentEnv() {
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

    private startBot() {
        if (Boolean.valueOf(System.properties["ircbot.disable"])) return
        ircbot = new GircBotBuilder()
        Map configMap = grailsApplication.config.irclog.ircbot.flatten()
        configMap.reactors << new Logger(irclogAppendService)
        ircbot.config(configMap).start()
    }

    private stopBot() {
        ircbot?.stop()
    }
}
