package irclog

import groovy.transform.ToString

@ToString
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

    static transients = ["repassword"]

    static hasMany = [channels: Channel, roles: Role]

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

        // In this application, the relation between person and role is one-to-one.
        // But spring-security requires has-many relationship, so it is.
        // TODO in case of the hasMany's field, it seems that nullable:true is default...
        roles nullable: false, size: 1..1
    }

    static mapping = {
        roles column: 'person_id', joinTable: 'role_person'
        channels column: 'person_channels_id', joinTable: 'person_channel'
    }

    boolean isAdmin() {
        return roles?.any { it.name == Role.ADMIN }
    }

    def beforeInsert() {
        encodePassword()
        addUserRole()
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

    private void addUserRole() {
        if (roles) return
        def role = Role.findByName(Role.USER)
        if (!role) throw new RuntimeException("User role not found in database")
        addToRoles(role)
    }
}
