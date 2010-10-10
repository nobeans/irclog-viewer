package irclog

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken as AuthToken
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class PersonService {

    boolean transactional = true

    def springSecurityService

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
            person.password = springSecurityService.encodePassword(rawPassword)
            person.repassword = person.password
            if (person.hasErrors()) return person
        }

        // デフォルトロールに関連づける。
        def defaultRoleName = ConfigurationHolder.config.irclog.security.defaultRole
        def role = Role.findByName(defaultRoleName)
        if (!role) {
            throw new RuntimeException("Default role not found in database: $defaultRoleName")
        }
        role.addToPersons(person)
        if (role.hasErrors()) return person

        // 新規登録に成功した場合は、そのままログインする。
        springSecurityService.reauthenticate(person.loginName)

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
            person.password = springSecurityService.encodePassword(params.password)
            person.repassword = person.password
            if (person.hasErrors()) return person
        }

        // 更新に成功した場合は、セッション上のユーザ情報を更新する。
        springSecurityService.reauthenticate(person.loginName)

        return person
    }
}
