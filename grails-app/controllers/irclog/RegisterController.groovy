package irclog

/**
 * ユーザによるユーザ情報登録・参照・編集のためのコントローラ。
 */
class RegisterController {

    def personService

    static allowedMethods = [save:'POST', update:'POST']

    def show() {
        withLoginPerson { person ->
            [person:person]
        }
    }

    def edit() {
        withLoginPerson { person ->
            // DB上にはrepasswordは存在しないので、画面上の初期表示のためにpasswordからコピーする。
            person.repassword = person.password

            [person:person]
        }
    }

    def update() {
        withLoginPerson { person ->
            person = personService.update(person, params)
            if (!person.hasErrors()) {
                flash.message = 'register.updated'
                redirect(action:'show', id:person.id)
            } else {
                render(view:'edit', model:[person:person])
            }
        }
    }

    def create() {
        [person:new Person()]
    }

    def save() {
        // 未ログインかどうか。
        if (request.isLoggedIn) {
            log.info('ログイン済みのため、ユーザ情報参照にリダイレクトします。')
            redirect(action:'show')
            return
        }

        def person = personService.create(params)
        if (!person.hasErrors()) {
            flash.message = "register.created"
            redirect(action:'show')
        } else {
            render(view:'create', model:[person:person])
        }
    }

    private withLoginPerson(closure) {
        def personId = request.loginUserDomain?.id // TODO
        def person = Person.get(personId)
        if (!person) {
            flash.errors = ["register.not.found"]
            flash.args = [personId]
            redirect(controller:'top')
            return
        }
        closure(person)
    }
}
