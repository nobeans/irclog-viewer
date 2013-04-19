package irclog

import grails.test.mixin.TestFor
import irclog.test.ConstraintUnitSpec
import irclog.utils.DomainUtils
import spock.lang.Unroll

@TestFor(Person)
class PersonSpec extends ConstraintUnitSpec {

    def setup() {
        mockForConstraintsTests(Person, [
            DomainUtils.createPerson(
                loginName: 'EXISTED_loginName',
                realName: 'EXISTED_realName',
            )
        ])
    }

    def "validate: DomainUtils' default values are all valid"() {
        given:
        Person person = DomainUtils.createPerson()

        expect:
        person.validate()
    }

    @Unroll
    def "validate: #field is #error when value is '#value'"() {
        given:
        Person person = DomainUtils.createPerson()
        person[field] = value
        person.repassword = person.password // not to confirm the mismatch them on this test case

        expect:
        validateConstraints(person, field, error)

        where:
        field       | error      | value
        'loginName' | 'nullable' | null
        'loginName' | 'blank'    | ''
        'loginName' | 'unique'   | 'EXISTED_loginName'
        'loginName' | 'valid'    | 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-'
        'loginName' | 'matches'  | 'あabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-'
        'loginName' | 'matches'  | getLongString(2)
        'loginName' | 'valid'    | getLongString(3)
        'loginName' | 'valid'    | getLongString(100)
        'loginName' | 'maxSize'  | getLongString(101)
        'realName'  | 'nullable' | null
        'realName'  | 'blank'    | ''
        'realName'  | 'unique'   | 'EXISTED_realName'
        'realName'  | 'valid'    | getLongString(100)
        'realName'  | 'maxSize'  | getLongString(101)
        'password'  | 'nullable' | null
        'password'  | 'blank'    | ''
        'password'  | 'minSize'  | getLongString(5)
        'password'  | 'valid'    | getLongString(6)
        'password'  | 'valid'    | 'x' * 100
        'password'  | 'maxSize'  | getLongString(101)
        'color'     | 'nullable' | null
        'color'     | 'valid'    | '#000'
        'color'     | 'valid'    | '#123'
        'color'     | 'valid'    | '#456'
        'color'     | 'valid'    | '#789'
        'color'     | 'valid'    | '#abc'
        'color'     | 'valid'    | '#def'
        'color'     | 'valid'    | '#ABC'
        'color'     | 'valid'    | '#DEF'
        'color'     | 'valid'    | '#000000'
        'color'     | 'valid'    | '#123456'
        'color'     | 'valid'    | '#789000'
        'color'     | 'valid'    | '#abcdef'
        'color'     | 'valid'    | '#ABCDEF'
        'color'     | 'matches'  | '000'
        'role'      | 'nullable' | null
        'role'      | 'valid'    | DomainUtils.createRole()
    }

    @Unroll
    def "validate: password and repassword must be same: #password, #repassword"() {
        given:
        Person person = DomainUtils.createPerson(password: password, repassword: repassword)

        expect:
        validateConstraints(person, 'password', error)

        where:
        error       | password    | repassword
        'valid'     | '123456'    | '123456'
        'valid'     | '123456789' | '123456789'
        'validator' | '123456'    | '12345x'
    }

    @Unroll
    def "validate: each nicks must start with LOGIN_NAME: #nicks"() {
        given:
        Person person = DomainUtils.createPerson(loginName: 'LOGIN_NAME', nicks: nicks)

        expect:
        validateConstraints(person, 'nicks', error)

        where:
        error       | nicks
        'nullable'  | null
        'valid'     | 'LOGIN_NAME'
        'valid'     | 'LOGIN_NAME'.padRight(200, 'x')
        'maxSize'   | 'LOGIN_NAME'.padRight(201, 'x')
        'valid'     | 'LOGIN_NAMEabcdefghijklmnopqrstuvwxyz LOGIN_NAMEABCDEFGHIJKLMNOPQRSTUVWXYZ LOGIN_NAME0123456789_-'
        'matches'   | 'LOGIN_NAMEあいうえお'
        'valid'     | 'LOGIN_NAME_DUPLICATED LOGIN_NAME_DUPLICATED'
        'validator' | 'NOT_STARTING_WITH_LOGIN_NAME'
        'validator' | 'LOGIN_NAME_VALID NOT_STARTING_WITH_LOGIN_NAME LOGIN_NAME_VALID2'
    }

    @Unroll
    def "isAdmin: a person who has #role #isAdmin an admin"() {
        given:
        Person person = DomainUtils.createPerson(role: role)

        expect:
        person.isAdmin() == (isAdmin == 'is')

        where:
        role      | isAdmin
        adminRole | 'is'
        userRole  | "isn't"
        null      | "isn't"
    }
}
