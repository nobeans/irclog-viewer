import org.springframework.security.providers.UsernamePasswordAuthenticationToken as AuthToken
import org.springframework.security.context.SecurityContextHolder as SCH

/**
 * ユーザによるユーザ情報登録・参照・編集のためのコントローラ。
 */
class RegisterController extends Base {

    def authenticateService
    def daoAuthenticationProvider

    def allowedMethods = [save:'POST', update:'POST']

    def show = {
        withLoginPerson { person ->
            [person:person]
        }
    }
 
    def edit = {
        withLoginPerson { person ->
            [person:person]
        }
    }
 
    def update = {
        withLoginPerson { person ->
            person.properties = params

            // if user want to change password. leave password field blank, password will not change.
            if (params.password && params.password.length() > 0 && params.repassword && params.repassword.length() > 0) {
                if (params.password == params.repassword) {
                    person.password = authenticateService.passwordEncoder(params.password)
                }
                else {
                    person.password = ''
                    flash.errors = ['The passwords you entered do not match.']
                    render(view:'edit', model:[person: person])
                    return
                }
            }
            if (person.save()) {
                redirect(action:'show', id:person.id)
            }
            else {
                render(view:'edit', model:[person:person])
            }
        }
    }

    def create = {
        [person:new Person()]
    }

    def save = {
        if (isLoggedIn) {
            log.info("${authenticateService.userDomain()} user hit the register page")
            redirect(action: 'show')
            return
        }

        def person = new Person(params)

        def role = Role.findByName(authenticateService.securityConfig.security.defaultRole)
        if (!role) {
            person.password = ''
            flash.message = 'Default Role not found.'
            redirect(controller:'top')
            return 
        }

        if (params.password != params.repassword) {
            person.password = ''
            flash.message = 'The passwords you entered do not match.'
            redirect(action:'create', person:person)
            return
        }

        def pass = authenticateService.passwordEncoder(params.password)
        person.password = pass
        person.enabled = true
        if (person.save()) {
            role.addToPersons(person)
            person.save(flush: true)

            def auth = new AuthToken(person.loginName, params.password)
            def authtoken = daoAuthenticationProvider.authenticate(auth)
            SCH.context.authentication = authtoken
            redirect(controller:'top')
        }
        else {
            person.password = ''
            redirect(action:'create', person:person)
        }
    }

    private withLoginPerson(closure) {
        def person = Person.get(loginUserDomain?.id)
        if (!person) {
            flash.message = "[Illegal Access] User not found with id ${params.id}"
            redirect(controller:'top')
            return
        }
        closure(person)
    }

}
