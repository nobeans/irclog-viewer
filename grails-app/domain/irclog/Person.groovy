package irclog

class Person {

    def springSecurityService

    String loginName
    String realName
    String password
    def repassword // for confirmation (not storing to database)
    String nicks // commpa separated nicks
    String color // for coloring on screen

    boolean enabled   // user staus (wether an user can login)
    final accountExpired = false
    final accountLocked = false
    final passwordExpired = false

    static hasMany = [channels:Channel, roles:Role]

    static constraints = {
        loginName(blank:false, matches:"[a-zA-Z0-9_-]{3,}", unique:true, maxSize:100)
        realName(blank:false, unique:true, maxSize:100)
        password(blank:false, minSize:6, maxSize:100, validator:{ val, obj -> obj.repassword == val })
        nicks(matches:"[ 0-9a-zA-Z_-]+", validator:{ val, obj ->
            val == "" || val.split(/ +/).every{ it.startsWith(obj.loginName) }
        }, maxSize:200)
        color(matches:"#[0-9A-Fa-f]{3}|#[0-9A-Fa-f]{6}")
        enabled()
        channels()
    }

    static mapping = {
        roles(column:'person_id', joinTable:'role_person')
        channels(column:'person_channels_id', joinTable:'person_channel')
    }

    boolean isAdmin() {
        return (roles?.find{it.name == Role.ADMIN}) != null
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
