package irclog

class PersonService {

    static transactional = true

    def springSecurityService

    def create(params) {
        // パラメタを反映したPersonインスタンスを生成する。
        def person = new Person(params)

        // デフォルトロールに関連づける。
        def role = Role.findByName(Role.USER)
        if (!role) {
            throw new RuntimeException("User role not found in database")
        }
        person.addToRoles(role)

        // パスワード文字列をハッシュに変換する。
        if (person.validate()) {
            encodePassword(person)
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
        if (person.validate() && person.password != currentEncodedPassword) {
            encodePassword(person)
        }

        // 保存する。
        person.save()

        return person
    }

    private Person encodePassword(person) {
        person.password = springSecurityService.encodePassword(person.password)
        person.repassword = springSecurityService.encodePassword(person.repassword)
        return person
    }
}
