package irclog

/**
 * 管理者用のユーザ管理コントローラ。
 */
class PersonController {

    def springSecurityService
    def personService

    def index() { redirect(action:'list', params:params) }

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list() {
        def defaultMax = grailsApplication.config.irclog.viewer.defaultMax
        params.max    = params.max?.toInteger() ? Math.min(params.max?.toInteger(), defaultMax) : defaultMax
        params.offset = params.offset?.toInteger() ?: 0
        params.sort   = params.sort ?: "loginName"
        params.order  = params.order ?: "asc"
        [
            personList: Person.list(params),
            personCount: Person.count()
        ]
    }

    def show() {
        withPerson(params.id) { person ->
            return [person:person]
        }
    }

    def delete() {
        withPerson(params.id) { person ->
            if (person.id == request.loginUserDomain?.id) {
                flash.message = "person.deleted.loggedInUser.error"
                flash.args = [params.id]
                redirect(action:'list')
                return
            }
            person.delete()
            flash.message = "person.deleted"
            flash.args = [params.id]
            redirect(action:'list')
        }
    }

    def edit() {
        withPerson(params.id) { person ->
            // DB上にはrepasswordは存在しないので、画面上の初期表示のためにpasswordからコピーする。
            person.repassword = person.password

            return [person:person]
        }
    }

    def update() {
        withPerson(params.id) { person ->
            // 更新する。
            person = personService.update(person, params)
            if (person.hasErrors()) {
                render(view:'edit', model:[person:person])
                return
            }

            flash.message = "person.updated"
            redirect(action:'show', id:person.id)
        }
    }

    def create() {
        return [person:new Person()]
    }

    def save() {
        // デフォルトロールを取得する。
        def role = Role.findByName(grailsApplication.config.irclog.security.defaultRole)
        if (!role) {
            flash.message = 'register.defaultRoleNotFound.'
            redirect(controller:'top')
            return
        }

        // 登録する。
        def person = personService.create(params)
        if (person.hasErrors()) {
            render(view:'create', model:[person:person])
            return
        }

        flash.message = "person.created"
        redirect(action:'show', id:person.id)
    }

    def toAdmin() {
        withPerson(params.id) { person ->
            person.removeFromRoles(Role.findByName("ROLE_USER"))
            person.addToRoles(Role.findByName("ROLE_ADMIN"))
            flash.message = "person.toAdmin.roleChanged"
            redirect(action:'show', id:person.id)
        }
    }

    def toUser() {
        withPerson(params.id) { person ->
            if (person.id == request.loginUserDomain?.id) {
                flash.message = "person.toUser.loggedInUser.error"
                flash.args = [params.id]
                redirect(action:'show', id:person.id)
                return
            }
            person.removeFromRoles(Role.findByName("ROLE_ADMIN"))
            person.addToRoles(Role.findByName("ROLE_USER"))
            flash.message = "person.toUser.roleChanged"
            redirect(action:'show', id:person.id)
        }
    }

    private withPerson(personId, closure) {
        def person = Person.get(personId)
        if (!person) {
            flash.errors = ["person.not.found"]
            flash.args = [personId]
            redirect(action:'list')
            return
        }
        return closure(person)
    }
}
