package irclog

import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * 管理者用のユーザ管理コントローラ。
 */
class PersonController {

    def springSecurityService

    def index = { redirect(action:list, params:params) }
    
    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete:'POST', save:'POST', update:'POST']
    
    def list = {
        def defaultMax = ConfigurationHolder.config.irclog.viewer.defaultMax
        params.max    = params.max?.toInteger() ? Math.min(params.max?.toInteger(), defaultMax) : defaultMax
        params.offset = params.offset?.toInteger() ?: 0
        params.sort   = params.sort ?: "loginName"
        params.order  = params.order ?: "asc"
        [
            personList: Person.list(params),
            personCount: Person.count()
        ]
    }
    
    def show = {
        withPerson(params.id) { person ->
            [person:person]
        }
    }
    
    def delete = {
        withPerson(params.id) { person ->
            if (person.id == request.loginUserDomain?.id) {
                flash.message = "person.deleted.loggedInUser.error"
                flash.args = [params.id]
                redirect(action:list)
                return
            }
            
            Role.list().each { role ->
                role.removeFromPersons(person)
            }
            person.delete()
            flash.message = "person.deleted"
            flash.args = [params.id]
            redirect(action:list)
        }
    }
    
    def edit = {
        withPerson(params.id) { person ->
            // DB上にはrepasswordは存在しないので、画面上の初期表示のためにpasswordからコピーする。
            person.repassword = person.password
            
            [person:person]
        }
    }
    
    def update = {
        withPerson(params.id) { person ->
            def currentEncodedPassword = person.password
            person.properties = params
            if (person.save()) {
                // 素のパスワード文字列に対してバリデーションはOKなので、ハッシュに変換する。
                if (currentEncodedPassword != person.password) {
                    person.password = springSecurityService.encodePassword(params.password)
                }
                redirect(action:'show', id:person.id)
            } else {
                render(view:'edit', model:[person:person])
            }
        }
    }
    
    def create = {
        [person:new Person()]
    }
    
    def save = {
        // デフォルトロールを取得する。
        def role = Role.findByName(ConfigurationHolder.config.irclog.security.defaultRole)
        if (!role) {
            flash.message = 'register.defaultRoleNotFound.'
            redirect(controller:'top')
            return
        }
        
        def person = new Person(params)
        role.addToPersons(person)
        if (person.save()) {
            // 素のパスワード文字列に対してバリデーションはOKなので、ハッシュに変換する。
            person.password = springSecurityService.encodePassword(params.password)
            
            flash.message = "person.created"
            redirect(action:show, id:person.id)
        } else {
            render(view:'create', model:[person:person])
        }
    }
    
    def toAdmin = {
        withPerson(params.id) { person ->
            Role.list().each { role ->
                if (role.name == "ROLE_ADMIN") {
                    role.addToPersons(person)
                } else {
                    role.removeFromPersons(person)
                }
            }
            flash.message = "person.toAdmin.roleChanged"
            redirect(action:show, id:person.id)
        }
    }
    
    def toUser = {
        withPerson(params.id) { person ->
            if (person.id == request.loginUserDomain?.id) {
                flash.message = "person.toUser.loggedInUser.error"
                flash.args = [params.id]
                redirect(action:show, id:person.id)
                return
            }
            
            Role.list().each { role ->
                if (role.name == "ROLE_USER") {
                    role.addToPersons(person)
                } else {
                    role.removeFromPersons(person)
                }
            }
            flash.message = "person.toUser.roleChanged"
            redirect(action:show, id:person.id)
        }
    }
    
    private withPerson(personId, closure) {
        def person = Person.get(personId)
        if (!person) {
            flash.errors = ["person.not.found"]
            flash.args = [personId]
            redirect(action:list)
            return
        }
        closure(person)
    }
}
