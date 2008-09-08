class BootStrap {

     def init = { servletContext ->
        if (Role.count() == 0) {
            assert new Role(name:'ROLE_USER',  description:'一般ユーザ').save() != null
            assert new Role(name:'ROLE_ADMIN', description:'管理者').save() != null
        }
     }

     def destroy = {
     }
} 
