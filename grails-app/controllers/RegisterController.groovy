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
            person.password = ''    // パスワードの重複エンコードを防止するための一番手抜きな方法
            person.repassword = ''
            [person:person]
        }
    }
 
    def update = {
        withLoginPerson { person ->
            person.properties = params
            if (person.save()) {
                // 素のパスワード文字列に対してバリデーションはOK.
                person.password = authenticateService.passwordEncoder(params.password)
                if (person.save(validate:false)) { // バリデーションをOFFにして変換されたパスワードを保存する。
                    flash.message = "person.updated"
                    redirect(action:'show', id:person.id)
                    return // 成功した場合
                }
            }
            // 失敗した場合
            person.password = ''    // パスワードの重複エンコードを防止するための一番手抜きな方法
            person.repassword = ''
            render(view:'edit', model:[person:person])
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
            person.password = ''
            flash.message = 'register.defaultRoleNotFound.'
            redirect(controller:'top')
            return 
        }

        def person = new Person(params)
        person.enabled = true
        role.addToPersons(person)
        if (person.save()) {
            // 素のパスワード文字列に対してバリデーションはOK.
            person.password = authenticateService.passwordEncoder(params.password)
            if (person.save(validate:false)) { // バリデーションをOFFにして変換されたパスワードを保存する。
                // 新規登録に成功した場合は、そのままログインする。
                def authtoken = daoAuthenticationProvider.authenticate(new AuthToken(person.loginName, params.password))
                SCH.context.authentication = authtoken

                flash.message = "person.created"
                redirect(controller:'top')
                return // 成功した場合
            }
        }
        // 失敗した場合
        person.password = ''    // パスワードの重複エンコードを防止するための一番手抜きな方法
        person.repassword = ''
        render(view:'create', model:[person:person])
    }

    private withLoginPerson(closure) {
        def personId = loginUserDomain?.id
        def person = Person.get(personId)
        if (!person) {
            flash.message = "person.not.found"
            flash.args = [personId]
            redirect(controller:'top')
            return
        }
        closure(person)
    }

}
