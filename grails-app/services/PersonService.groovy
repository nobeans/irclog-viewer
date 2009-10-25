import org.grails.plugins.springsecurity.service.AuthenticateService
import org.springframework.security.providers.UsernamePasswordAuthenticationToken as AuthToken
import org.springframework.security.context.SecurityContextHolder as SCH
import org.springframework.validation.FieldError

class PersonService {

    boolean transactional = true
	def authenticateService

    def create(params) {
        def person = new Person(params)

        // 自分で登録したときは即有効にする。
        person.enabled = true

        // 保存する。
        if (person.hasErrors()) return person // 事前
        person.save()
        if (person.hasErrors()) return person // 事後

        // 素のパスワード文字列に対してバリデーションはOKなので、ハッシュに変換する。
        def rawPassword = person.password // 後でログイン用に使うため、退避しておく。
        if (person.password && person.repassword && person.password == person.repassword) {
            person.password = authenticateService.passwordEncoder(rawPassword)
            person.repassword = person.password
            if (person.hasErrors()) return person
        }

        // デフォルトロールに関連づける。
        def role = Role.findByName(authenticateService.securityConfig.security.defaultRole)
        role.addToPersons(person)
        if (role.hasErrors()) return person

        // 新規登録に成功した場合は、そのままログインする。
        SCH.context.authentication = new AuthToken(person.loginName, rawPassword)

        return person
    }

    def update(person, params) {
        // 入力を反映する。
        def currentEncodedPassword = person.password
        person.properties = params

        // 保存する。
        if (person.hasErrors()) return person // 事前
        person.save()
        if (person.hasErrors()) return person // 事後

        // 素のパスワード文字列に対してバリデーションはOKなので、ハッシュに変換する。
        if (currentEncodedPassword != person.password) {
            person.password = authenticateService.passwordEncoder(params.password)
            person.repassword = person.password
            if (person.hasErrors()) return person
        }

        // 更新に成功した場合は、セッション上のユーザ情報を更新する。
        // FIXME:Acegiの作法がわからなかったため、かなり強引な方法で実装している。
        SCH.context.authentication.principal.domainClass.realName = person.realName

        return person
    }
}
