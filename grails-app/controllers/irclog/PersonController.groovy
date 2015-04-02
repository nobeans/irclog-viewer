package irclog

/**
 * For administrator only.
 */
class PersonController {

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

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
                flash.message = message(code: "person.deleted.loggedInUser.error", args: [params.id])
                redirect(action: 'list')
                return
            }
            person.delete()
            flash.message = message(code: "default.deleted.message", args: [message(code: "person.label"), params.id])
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
            person.save()
            if (person.hasErrors()) {
                render(view: 'edit', model: [person: person])
                return
            }

            flash.message = message(code: "default.updated.message", args: [message(code: "person.label"), person.id])
            redirect(action: 'show', id: person.id)
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

        flash.message = message(code: "default.created.message", args: [message(code: "person.label"), person.id])
        redirect(action: 'show', id: person.id)
    }

    def toAdmin() {
        withPerson(params.id) { person ->
            person.toAdmin().save()
            flash.message = message(code: "person.toAdmin.roleChanged.message")
            redirect(action: 'show', id: person.id)
        }
    }

    def toUser() {
        withPerson(params.id) { person ->
            if (person == authenticatedUser) {
                flash.message = message(code: "person.toUser.loggedInUser.error", args: [params.id])
                redirect(action: 'show', id: person.id)
                return
            }
            person.toUser().save()
            flash.message = message(code: "person.toUser.roleChanged.message")
            redirect(action: 'show', id: person.id)
        }
    }

    private withPerson(personId, closure) {
        def person = Person.get(personId)
        if (!person) {
            flash.errors = [message(code: "default.not.found.message", args: [message(code: "person.label"), personId])]
            redirect(action: 'list')
            return
        }
        return closure(person)
    }
}
