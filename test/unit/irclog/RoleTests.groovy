package irclog

import grails.test.mixin.*
import org.junit.*
import static irclog.utils.DomainUtils.*

@TestFor(Role)
class RoleTests {

    @Before
    void setUp() {
        mockForConstraintsTests(Role, [
            createRole(name:'EXISTED_NAME')
        ])
    }

    @Test
    void validate_Ok() {
        def role = createRole()
        assert role.validate()
    }

    @Test
    void validate_NG_name_blank() {
        def role = createRole(name:'')
        assert role.validate() == false
        assert role.errors['name'] == 'blank'
    }

    @Test
    void validate_NG_name_unique() {
        def role = createRole(name:'EXISTED_NAME')
        assert role.validate() == false
        assert role.errors['name'] == 'unique'
    }
}
