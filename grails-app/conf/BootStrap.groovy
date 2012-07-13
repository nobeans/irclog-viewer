import grails.util.Environment
import irclog.Channel
import irclog.Role
import irclog.Person
import irclog.utils.DateUtils
import static irclog.utils.DomainUtils.*

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
        if (!Role.findByName(Role.USER)) createRole(name:Role.USER).save(failOnError:true)
        if (!Role.findByName(Role.ADMIN)) createRole(name:Role.ADMIN).save(failOnError:true)
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
                roles: Role.findAllByName(Role.ADMIN),
            ).save(failOnError:true)
        }
    }

    private setupForDevelopmentEnv() {
        if (Environment.isDevelopmentMode()) {
            def channelTest1 = createChannel(name:"#test1", isPrivate:true).save(failOnError:true)
            def channelTest2 = createChannel(name:"#test2", isPrivate:false, secretKey:"").save(failOnError:true)
            def channelTest3 = createChannel(name:"#test3", isPrivate:true).save(failOnError:true)
            30.times {
                (-2..0).each { dateDelta ->
                    def today = DateUtils.today
                    createIrclog(channelName:"#test1", channel:channelTest1, type:"PRIVMSG", time:today + dateDelta).save(failOnError:true)

                    createIrclog(channelName:"#test2", channel:channelTest2, type:"TOPIC", time:today + dateDelta).save(failOnError:true)
                    createIrclog(channelName:"#test2", channel:channelTest2, type:"PRIVMSG", time:today + dateDelta).save(failOnError:true)
                    createIrclog(channelName:"#test2", channel:channelTest2, type:"NOTICE", time:today + dateDelta).save(failOnError:true)
                    createIrclog(channelName:"#test2", channel:channelTest2, type:"JOIN", time:today + dateDelta).save(failOnError:true)

                    createIrclog(channelName:"#test3", channel:channelTest3, type:"PRIVMSG", time:today + dateDelta, message:'つhttp://localhost:8080/irclog/!"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~  !"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~').save(failOnError:true)
                    createIrclog(channelName:"#test3", channel:channelTest3, type:"TOPIC", time:today + dateDelta, message:'つhttp://localhost:8080/irclog/!"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~   !"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~').save(failOnError:true)
                    createIrclog(channelName:"#test3", channel:channelTest3, type:"PRIVMSG", time:today + dateDelta, message:'!"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~ ').save(failOnError:true)
                    createIrclog(channelName:"#test3", channel:channelTest3, type:"TOPIC", time:today + dateDelta, message:'!"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~ ').save(failOnError:true)
                }
            }
        }
    }
}
