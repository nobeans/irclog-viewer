package irclog

/**
 * ユーザによるユーザ情報登録・参照・編集のためのコントローラ。
 */
class RegisterController {

    def springSecurityService

    static allowedMethods = [save: 'POST', update: 'POST']

    def show() {
        withLoginPerson { person ->
            [person: person]
        }
    }

    def edit() {
        withLoginPerson { person ->
            // DB上にはrepasswordは存在しないので、画面上の初期表示のためにpasswordからコピーする。
            person.repassword = person.password

            [person: person]
        }
    }

    def update() {
        withLoginPerson { person ->
            // ユーザによる情報更新の場合は一部のプロパティは変更不可
            params.remove 'enabled'

            // 更新する。
            person.properties = params
            person.save()
            if (person.hasErrors()) {
                render(view: 'edit', model: [person: person])
                return
            }

            // 更新に成功した場合は、セッション上のユーザ情報を更新する。
            springSecurityService.reauthenticate(person.loginName)

            flash.message = 'register.updated'
            redirect(action: 'show', id: person.id)
        }
    }

    def create() {
        [person: new Person()]
    }

    def save() {
        // 未ログインかどうか。
        if (loggedIn) {
            log.info('ログイン済みのため、ユーザ情報参照にリダイレクトします。')
            redirect(action: 'show')
            return
        }

        // 自分で登録したときは即有効にする。
        params.enabled = true

        // 登録する。
        def person = new Person(params)
        person.toUser()
        person.save()
        if (person.hasErrors()) {
            render(view: 'create', model: [person: person])
            return
        }

        // 新規登録に成功した場合は、そのままログインする。
        springSecurityService.reauthenticate(person.loginName)

        flash.message = "register.created"
        redirect(action: 'show')
    }

    private withLoginPerson(closure) {
        def person = authenticatedUser
        if (!person) {
            flash.errors = ["register.not.found"]
            flash.args = [personId]
            redirect(controller: 'top')
            return
        }
        closure(person)
    }
}
