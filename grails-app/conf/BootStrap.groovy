import irclog.Irclog
import irclog.Person
import irclog.Role
import irclog.VertxService
import irclog.ircbot.Ircbot
import irclog.utils.DateUtils

import static irclog.utils.DomainUtils.*

class BootStrap {

    Ircbot ircbot
    VertxService vertxService

    def init = { servletContext ->
        setupRolesIfNotExists()
        setupDefaultAdminUserIfNotExists()
        environments {
            development {
                setupDemoData()
                ircbot.start()
            }
            production {
                ircbot.start()
            }
        }
        vertxService.start()
    }

    def destroy = {
        ircbot.stop()
        vertxService.stop()
    }

    private static void setupRolesIfNotExists() {
        if (!Role.findByName(Role.USER)) createRole(name: Role.USER).save(failOnError: true, flush: true)
        if (!Role.findByName(Role.ADMIN)) createRole(name: Role.ADMIN).save(failOnError: true, flush: true)
    }

    private static void setupDefaultAdminUserIfNotExists() {
        if (!Person.findByLoginName("admin")) {
            createPerson(
                loginName: "admin",
                realName: "Administrator",
                password: "admin00",
                enabled: true,
                nicks: "admin_test",
                color: "#f00",
                role: Role.findByName(Role.ADMIN)
            ).save(failOnError: true, flush: true)
        }
    }

    private static setupDemoData() {
        def channels = [
            createChannel(name: "#test1", isPrivate: true),
            createChannel(name: "#test2", isPrivate: false, secretKey: ""),
            createChannel(name: "#test3", isPrivate: true),
            createChannel(name: "#test4", isPrivate: false, secretKey: ""),
        ].collect { it.save(failOnError: true, flush: false) }

        3.times { id ->
            def user = createPerson(
                loginName: "user0${id}",
                realName: "User-0${id}",
                password: "user0${id}",
                enabled: true,
                nicks: "user0${id}_test",
                color: "#0f${id * 3}",
                role: Role.findByName(Role.USER)
            ).save(failOnError: true, flush: true)
            channels.each { channel ->
                channel.addToPersons(user)
            }
        }

        (0..7).each { dateDelta ->
            channels.eachWithIndex { channel, index ->
                // #test4 has no logs
                if (channel.name == "#test4") return

                Irclog.ALL_TYPES.each { type ->
                    (index + dateDelta + 1).times {
                        def time = DateUtils.today - dateDelta
                        createIrclog(
                            channelName: channel.name,
                            channel: channel,
                            type: type,
                            time: time
                        ).save(failOnError: true, flush: false)
                    }
                }
            }
        }
        Irclog.withSession { it.flush() }

        // http/https URL
        def irclogs = Irclog.findAllByType('PRIVMSG', [sort: 'time', order: 'desc', max: 6])
        irclogs[0].message = "http://yahoo.co.jp/"
        irclogs[1].message = "http://yahoo.co.jp/ http://google.co.jp"
        irclogs[2].message = "I sometimes use http://yahoo.co.jp/ and am always using http://google.co.jp/"
        irclogs[3].message = "http://yahoo.co.jp/ https://google.co.jp"
        irclogs[4].message = "https://www.google.co.jp/?q=%E3%81%82%E3%81%84%E3%81%86%E3%81%88%E3%81%8A"
        irclogs[5].message = "<script>alert('XSS');</script>"
        Irclog.withSession { it.flush() }
    }
}
