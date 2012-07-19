package irclog

import groovy.transform.ToString
import groovy.transform.EqualsAndHashCode

@ToString
@EqualsAndHashCode(includes = "id")
class Person {

    def springSecurityService

    String loginName
    String realName
    String password
    String repassword // for confirmation (not storing to database)
    String nicks // whitespace separated nicks
    String color // for coloring on screen

    Boolean enabled = true  // user status (whether an user can login)
    final accountExpired = false
    final accountLocked = false
    final passwordExpired = false

    Role role

    static transients = ['repassword', 'roles']

    static hasMany = [channels: Channel]

    static constraints = {
        loginName blank: false, unique: true, maxSize: 100, matches: /[a-zA-Z0-9_-]{3,}/
        realName blank: false, unique: true, maxSize: 100
        password blank: false, minSize: 6, maxSize: 100, validator: { val, obj -> val == obj.repassword }
        repassword bindable: true
        nicks maxSize: 200, matches: /[ 0-9a-zA-Z_-]+/, validator: { val, obj ->
            val == "" || val.split(/ +/).every {
                it.startsWith(obj.loginName ?: '')
            }
        }
        color matches: /#[0-9A-Fa-f]{3}|#[0-9A-Fa-f]{6}/
        enabled()
        channels()

        role nullable: false, bindable: true
    }

    def getRoles() {
        // In this application, the relation between person and role is one-to-one.
        [role]
    }

    static mapping = {
        channels column: 'person_channels_id', joinTable: 'person_channel'
    }

    boolean isAdmin() {
        return role.name == Role.ADMIN
    }

    def beforeInsert() {
        encodePassword()
        setDefaultRole()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }

    private void encodePassword() {
        password = springSecurityService.encodePassword(password)
        repassword = password
    }

    private void setDefaultRole() {
        def user = Role.findByName(Role.USER)
        if (!role) throw new RuntimeException("User role not found in database")
        role = user
    }
}
