import grails.util.GrailsUtil
import irclog.Channel
import irclog.Role
import irclog.Person

class BootStrap {

    def springSecurityService

    def init = { servletContext ->
        setupRolesIfNotExists()
        setupDefaultAdminUserIfNotExists()

        // for Development
        if (GrailsUtil.isDevelopmentEnv()) { // only in development mode
            (1..3).each {
                assert createChannel("#test$it").save()
            }
        }
    }

    def destroy = {
    }

    /** ロール定義を追加する。 */
    private void setupRolesIfNotExists() {
        if (Role.count() == 0) {
            Role.withTransaction {
                assert new Role(name:'ROLE_USER',  description:'User').save() != null
                assert new Role(name:'ROLE_ADMIN', description:'Administrator').save() != null
            }
        }
    }

    /** 組み込みの管理者ユーザを追加する。[admin/admin] */
    private void setupDefaultAdminUserIfNotExists() {
        if (Person.findByLoginName("admin") == null) {
            Person.withTransaction {
                def admin = new Person(loginName:"admin", realName:"Administrator", password:springSecurityService.encodePassword("admin"), enabled:true, nicks:"", color:"")
                assert admin != null
                def role = Role.findByName("ROLE_ADMIN")
                assert role != null
                role.addToPersons(admin)
            }
        }
    }

    private Channel createChannel(name) {
        def channel = new Channel(name:name)
        channel.description = "説明文です"
        channel.isPrivate = true
        channel.isArchived = true
        channel.secretKey = "1234"
        channel
    }
}
