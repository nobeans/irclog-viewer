package irclog

/**
 * 管理者用のユーザ管理コントローラ。
 */
class PersonController {

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def springSecurityService

    def index() { redirect(action: 'list', params: params) }

    def list() {
        def defaultMax = grailsApplication.config.irclog.viewer.defaultMax
        params.max = params.max?.toInteger() ? Math.min(params.max?.toInteger(), defaultMax) : defaultMax
        params.offset = params.offset?.toInteger() ?: 0
        params.sort = params.sort ?: "loginName"
        params.order = params.order ?: "asc"
        [
            personList: Person.list(params),
            personCount: Person.count()
        ]
    }

    def show() {
        withPerson(params.id) { person ->
            return [person: person]
        }
    }

    def delete() {
        withPerson(params.id) { person ->
            if (person == authenticatedUser) {
                flash.message = "person.deleted.loggedInUser.error"
                flash.args = [params.id]
                redirect(action: 'list')
                return
            }
            person.delete()
            flash.message = "person.deleted"
            flash.args = [params.id]
            redirect(action: 'list')
        }
    }

    def edit() {
        withPerson(params.id) { person ->
            // DB上にはrepasswordは存在しないので、画面上の初期表示のためにpasswordからコピーする。
            person.repassword = person.password

            return [person: person]
        }
    }

    def update() {
        withPerson(params.id) { person ->
            // 更新する。
            person.properties = params

            sleep 5000 // DEBUG

            person.withOptimisticLock {
                person.save(flush: true)
                if (person.hasErrors()) {
                    render(view: 'edit', model: [person: person])
                    return
                }

                flash.message = "person.updated"
                redirect(action: 'show', id: person.id)

            }.onConflict {
                render(view: 'edit', model: [person: person])
            }
        }
    }

    def create() {
        return [person: new Person()]
    }

    def save() {
        def person = new Person(params)
        person.toUser()
        person.save()
        if (person.hasErrors()) {
            render(view: 'create', model: [person: person])
            return
        }

        flash.message = "person.created"
        redirect(action: 'show', id: person.id)
    }

    def toAdmin() {
        withPerson(params.id) { person ->
            person.toAdmin().save()
            flash.message = "person.toAdmin.roleChanged"
            redirect(action: 'show', id: person.id)
        }
    }

    def toUser() {
        withPerson(params.id) { person ->
            if (person == authenticatedUser) {
                flash.message = "person.toUser.loggedInUser.error"
                flash.args = [params.id]
                redirect(action: 'show', id: person.id)
                return
            }
            person.toUser().save()
            flash.message = "person.toUser.roleChanged"
            redirect(action: 'show', id: person.id)
        }
    }

    private withPerson(personId, closure) {
        def person = Person.get(personId)
        if (!person) {
            flash.errors = ["person.not.found"]
            flash.args = [personId]
            redirect(action: 'list')
            return
        }
        return closure(person)
    }
}
