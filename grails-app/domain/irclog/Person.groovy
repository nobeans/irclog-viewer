package irclog

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@EqualsAndHashCode(includes = "id")
class Person {

    def springSecurityService

    Long id // defined explicitly for EqualsAndHashCode
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
    static belongsTo = Channel

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

        role bindable: false
    }

    def getRoles() {
        // In this application, the relation between person and role is one-to-one.
        [role]
    }

    boolean isAdmin() {
        return role?.name == Role.ADMIN
    }

    def beforeInsert() {
        if (!password) {
            encodePassword()
        }
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }

    def afterLoad() {
        repassword = password
    }

    private void encodePassword() {
        password = springSecurityService.encodePassword(password)
        repassword = password
    }

    Person toUser() {
        role = Role.findByName(Role.USER)
        return this
    }

    Person toAdmin() {
        role = Role.findByName(Role.ADMIN)
        return this
    }
}
