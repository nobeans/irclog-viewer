package irclog.test

import irclog.Role
import irclog.utils.DomainUtils
import spock.lang.Specification

abstract class ConstraintUnitSpec extends Specification {

    String getLongString(Integer length) {
        def longString = ""
        length.times { longString += "a" }
        longString
    }

    Role getRole(String roleName) {
        DomainUtils.createRole(name: roleName)
    }

    Role getAdminRole() {
        getRole(Role.ADMIN)
    }

    Role getUserRole() {
        getRole(Role.USER)
    }

    void validateConstraints(obj, field, error) {
        def validated = obj.validate()
        if (error && error != 'valid') {
            assert !validated
            assert obj.errors[field]
            assert error == obj.errors[field]
        } else {
            assert !obj.errors[field]
        }
    }
}
