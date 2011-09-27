package irclog

class PersonService {

    static transactional = true

    def springSecurityService
    def grailsApplication

    def create(params) {
        // パラメタを反映したPersonインスタンスを生成する。
        def person = new Person(params)

        // デフォルトロールに関連づける。
        def defaultRoleName = grailsApplication.config.irclog.security.defaultRole
        def role = Role.findByName(defaultRoleName)
        if (!role) {
            throw new RuntimeException("Default role not found in database: $defaultRoleName")
        }
        person.addToRoles(role)

        // パスワード文字列をハッシュに変換する。
        if (person.password && person.repassword && person.password == person.repassword) {
            person.password = springSecurityService.encodePassword(person.password)
            person.repassword = person.password
        }

        // 保存する。
        person.save()

        return person
    }

    def update(person, params) {
        // 変更有無判定用に、現在のエンコード済みパスワードを退避しておく。
        def currentEncodedPassword = person.password

        // 入力を反映する。
        person.properties = params

        // パスワード文字列をハッシュに変換する。
        if (person.password && person.repassword && person.password == person.repassword && person.password != currentEncodedPassword) {
            person.password = springSecurityService.encodePassword(params.password)
            person.repassword = person.password
        }

        // 保存する。
        person.save()

        return person
    }
}
