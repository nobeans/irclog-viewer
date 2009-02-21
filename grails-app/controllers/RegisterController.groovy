import org.grails.plugins.springsecurity.service.AuthenticateService
import org.springframework.security.providers.UsernamePasswordAuthenticationToken as AuthToken
import org.springframework.security.context.SecurityContextHolder as SCH

/**
 * ユーザによるユーザ情報登録・参照・編集のためのコントローラ。
 */
class RegisterController extends Base {

	AuthenticateService authenticateService
    def daoAuthenticationProvider

    def allowedMethods = [save:'POST', update:'POST']

    def show = {
        withLoginPerson { person ->
            [person:person]
        }
    }
 
    def edit = {
        withLoginPerson { person ->
            // DB上にはrepasswordは存在しないので、画面上の初期表示のためにpasswordからコピーする。
            person.repassword = person.password

            [person:person]
        }
    }
 
    def update = {
        withLoginPerson { person ->
            def currentEncodedPassword = person.password
            person.properties = params
            if (person.save()) {
                // 素のパスワード文字列に対してバリデーションはOKなので、ハッシュに変換する。
                if (currentEncodedPassword != person.password) {
                    person.password = authenticateService.passwordEncoder(params.password)
                }

                // 更新に成功した場合は、セッション上のユーザ情報を更新する。
                // FIXME:Acegiの作法がわからなかったため、かなり強引な方法で実装している。
                SCH.context.authentication.principal.domainClass.realName = person.realName

                flash.message = 'register.updated'
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
        // 未ログインかどうか。
        if (isLoggedIn) {
            log.info('ログイン済みのため、ユーザ情報参照にリダイレクトします。')
            redirect(action:'show')
            return
        }

        // デフォルトロールを取得する。
        def role = Role.findByName(authenticateService.securityConfig.security.defaultRole)
        if (!role) {
            flash.message = 'register.defaultRoleNotFound'
            redirect(controller:'top')
            return 
        }

        def person = new Person(params)
        person.enabled = true // 自分で登録したときは即有効
        role.addToPersons(person)
        if (person.save()) {
            // 素のパスワード文字列に対してバリデーションはOKなので、ハッシュに変換する。
            person.password = authenticateService.passwordEncoder(params.password)

            // 新規登録に成功した場合は、そのままログインする。
            def authtoken = daoAuthenticationProvider.authenticate(new AuthToken(person.loginName, params.password))
            SCH.context.authentication = authtoken

            flash.message = "register.created"
            redirect(action:'show')
        } else {
            render(view:'create', model:[person:person])
        }
    }

    private withLoginPerson(closure) {
        def personId = loginUserDomain?.id
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
