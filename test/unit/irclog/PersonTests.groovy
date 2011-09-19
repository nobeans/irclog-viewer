package irclog

import grails.test.mixin.*
import org.junit.*
import static irclog.utils.DomainUtils.*

@TestFor(Person)
class PersonTests {

    @Before
    void setUp() {
        mockForConstraintsTests(Person, [
            createPerson(loginName:'EXISTED_loginName', realName:'EXISTED_realName')
        ])
    }

    @Test
    void validate_OK() {
        def person = createPerson()
        assert person.validate()
    }

    @Test
    void validate_NG_loginName_blank() {
        def person = createPerson(loginName:'')
        assert person.validate() == false
        assert person.errors['loginName'] == 'blank'
    }

    @Test
    void validate_NG_loginName_unique() {
        def person = createPerson(loginName:'EXISTED_loginName')
        assert person.validate() == false
        assert person.errors['loginName'] == 'unique'
    }

    @Test
    void validate_OK_loginName_matches() {
        assert createPerson(loginName:'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-').validate()
    }

    @Test
    void validate_NG_loginName_matches() {
        def person = createPerson(loginName:'あabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-')
        assert person.validate() == false
        assert person.errors['loginName'] == 'matches'
    }

    @Test
    void validate_NG_loginName_matches_less() {
        def person = createPerson(loginName:'x'*2)
        assert person.validate() == false
        assert person.errors['loginName'] == 'matches'
    }

    @Test
    void validate_OK_loginName_maxSize() {
        assert createPerson(loginName:'x'*3, realName:'test').validate()
        assert createPerson(loginName:'x'*100, realName:'test').validate()
    }

    @Test
    void validate_NG_loginName_maxSize_more() {
        def person = createPerson(loginName:'x'*101)
        assert person.validate() == false
        assert person.errors['loginName'] == 'maxSize'
    }

    @Test
    void validate_NG_realName_blank() {
        def person = createPerson(realName:'')
        assert person.validate() == false
        assert person.errors['realName'] == 'blank'
    }

    @Test
    void validate_NG_realName_unique() {
        def person = createPerson(realName:'EXISTED_realName')
        assert person.validate() == false
        assert person.errors['realName'] == 'unique'
    }

    @Test
    void validate_OK_realName_maxSize() {
        assert createPerson(realName:'x'*3).validate()
        assert createPerson(realName:'x'*100).validate()
    }

    @Test
    void validate_NG_realName_maxSize_more() {
        def person = createPerson(realName:'x'*101)
        assert person.validate() == false
        assert person.errors['realName'] == 'maxSize'
    }

    @Test
    void validate_NG_password_blank() {
        def person = createPerson(password:'')
        assert person.validate() == false
        assert person.errors['password'] == 'blank'
    }

    @Test
    void validate_OK_password_size() {
        assert createPerson(password:'x'*6).validate()
        assert createPerson(password:'x'*100).validate()
    }

    @Test
    void validate_NG_password_minSize() {
        def person = createPerson(password:'x'*5)
        assert person.validate() == false
        assert person.errors['password'] == 'minSize'
    }

    @Test
    void validate_NG_password_maxSize() {
        def person = createPerson(password:'x'*101)
        assert person.validate() == false
        assert person.errors['password'] == 'maxSize'
    }

    @Test
    void validate_OK_password_validator() {
        assert createPerson(password:'123456', repassword:'123456')
        assert createPerson(password:'1234567890', repassword:'1234567890')
    }

    @Test
    void validate_NG_password_validator() {
        def person = createPerson(password:'123456', repassword:'12345x')
        assert person.validate() == false
        assert person.errors['password'] == 'validator'
    }

    @Test
    void validate_OK_nicks_matches_maxSize_startsWith() {
        assert createPerson(loginName:'LOGIN_NAME', nicks:'LOGIN_NAMEabcdefghijklmnopqrstuvwxyz LOGIN_NAMEABCDEFGHIJKLMNOPQRSTUVWXYZ LOGIN_NAME0123456789_-').validate()
        assert createPerson(loginName:'LOGIN_NAME',  nicks:'LOGIN_NAME'+'x'*(200 - 'LOGIN_NAME'.size())).validate()
    }

    @Test
    void validate_NG_nicks_matches_invalidChar() {
        def person = createPerson(loginName:'LOGIN_NAME',  nicks:'LOGIN_NAMEあいうえお')
        assert person.validate() == false
        assert person.errors['nicks'] == 'matches'
    }

    @Test
    void validate_NG_nicks_matches_startsWith() {
        def person = createPerson(loginName:'LOGIN_NAME',  nicks:'abcd')
        assert person.validate() == false
        assert person.errors['nicks'] == 'validator'
    }

    @Test
    void validate_NG_nicks_matches_maxSize() {
        def person = createPerson(loginName:'LOGIN_NAME',  nicks:'LOGIN_NAME'+'x'*(200 - 'LOGIN_NAME'.size() + 1))
        assert person.validate() == false
        assert person.errors['nicks'] == 'maxSize'
    }

    @Test
    void validate_OK_color_matches() {
        assert createPerson(color:'#000').validate()
        assert createPerson(color:'#1ac').validate()
        assert createPerson(color:'#fff').validate()
        assert createPerson(color:'#000000').validate()
        assert createPerson(color:'#123456').validate()
        assert createPerson(color:'#abcdef').validate()
    }

    @Test
    void validate_NG_color_matches() {
        def person = createPerson(color:'000')
        assert person.validate() == false
        assert person.errors['color'] == 'matches'
    }

    @Test
    void validate_NG_roles_nullable() {
        def person = createPerson(roles:null)
        assert person.validate() == false
        assert person.errors['roles'] == 'nullable'
    }

    @Test
    void validate_NG_roles_size_empty() {
        def person = createPerson(roles:[])
        assert person.validate() == false
        assert person.errors['roles'] == 'size'
    }

    @Test
    void validate_NG_roles_size_multiple() {
        def person = createPerson(roles:[createRole(), createRole()])
        assert person.validate() == false
        assert person.errors['roles'] == 'size'
    }

}
