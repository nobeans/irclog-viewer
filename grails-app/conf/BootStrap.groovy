import org.grails.plugins.springsecurity.service.AuthenticateService

class BootStrap {

	AuthenticateService authenticateService

    def init = { servletContext ->
        // ロール定義を追加する。
        if (Role.count() == 0) {
            assert new Role(name:'ROLE_USER',  description:'User').save() != null
            assert new Role(name:'ROLE_ADMIN', description:'Administrator').save() != null
        }

        // 組み込みの管理者ユーザを追加する。[admin/admin]
        if (Person.findByLoginName("admin") == null) {
            def admin = new Person(loginName:"admin", realName:"Administrator", password:authenticateService.passwordEncoder("admin"), enabled:true, nicks:"", color:"")
            assert admin != null
            def role = Role.findByName("ROLE_ADMIN")
            assert role != null
            role.addToPersons(admin)
        }
    }

    def destroy = {
    }
} 
