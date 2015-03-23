package irclog

import grails.test.mixin.TestFor
import irclog.test.ConstraintUnitSpec
import irclog.utils.DomainUtils
import spock.lang.Unroll

@TestFor(Role)
class RoleSpec extends ConstraintUnitSpec {

    def setup() {
        DomainUtils.createRole(name: 'ROLE_EXISTED').save(failOnError: true, flush: true)
    }

    def "validate: DomainUtils' default values are all valid"() {
        given:
        Role role = DomainUtils.createRole()

        expect:
        role.validate()
    }

    @Unroll
    def "validate: #field is #error when value is '#value'"() {
        given:
        Role role = DomainUtils.createRole("$field": value)

        expect:
        validateConstraints(role, field, error)

        where:
        field  | error              | value
        'name' | 'valid'            | 'ROLE_USER'
        'name' | 'valid'            | 'ROLE_ADMIN'
        'name' | 'nullable'         | null
        'name' | 'blank'            | ''
        'name' | 'matches.invalid'  | 'NOT_STARTING_WITH_ROLE_PREFIX'
        'name' | 'matches.invalid'  | 'ROLE_ HAS SPACE'
        'name' | 'unique'           | 'ROLE_EXISTED'
        'name' | 'valid'            | 'ROLE_'.padRight(100, 'x')
        'name' | 'maxSize.exceeded' | 'ROLE_'.padRight(101, 'x')
    }
}
