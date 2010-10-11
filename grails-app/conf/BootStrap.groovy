import grails.util.GrailsUtil
import irclog.Channel
import irclog.Role
import irclog.Person
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
        if (Role.count() == 0) {
            Role.withTransaction {
                createRole(name:'ROLE_USER',  description:'User').saveSurely()
                createRole(name:'ROLE_ADMIN', description:'Administrator').saveSurely()
            }
        }
    }

    private void setupDefaultAdminUserIfNotExists() {
        if (Person.findByLoginName("admin") == null) {
            Person.withTransaction {
                def password = springSecurityService.encodePassword("admin")
                def admin = createPerson(loginName:"admin", realName:"Administrator", password:password, enabled:true, nicks:"", color:"").saveSurely()
                def role = Role.findByName("ROLE_ADMIN")
                assert role != null
                role.addToPersons(admin)
            }
        }
    }

    private setupForDevelopmentEnv() {
        if (GrailsUtil.isDevelopmentEnv()) { // only in development mode
            createChannel(name:"#test1", isPrivate:true).saveSurely()
            createChannel(name:"#test2", isPrivate:false, secretKey:"").saveSurely()
            createChannel(name:"#test3", isPrivate:true).saveSurely()
        }
    }
}
