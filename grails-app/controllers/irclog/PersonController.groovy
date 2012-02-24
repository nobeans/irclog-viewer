package irclog

/**
 * 管理者用のユーザ管理コントローラ。
 */
class PersonController {

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
            person.properties = params
            person.save()
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
        // ユーザロールを取得する。
        def role = Role.findByName(Role.USER)
        if (!role) {
            flash.message = 'register.userRoleNotFound.'
            redirect(controller:'top')
            return
        }

        // 登録する。
        def person = new Person(params)
        person.save()
        if (person.hasErrors()) {
            render(view:'create', model:[person:person])
            return
        }

        flash.message = "person.created"
        redirect(action:'show', id:person.id)
    }

    def toAdmin() {
        withPerson(params.id) { person ->
            person.removeFromRoles(Role.findByName(Role.USER))
            person.addToRoles(Role.findByName(Role.ADMIN))
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
            person.removeFromRoles(Role.findByName(Role.ADMIN))
            person.addToRoles(Role.findByName(Role.USER))
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
