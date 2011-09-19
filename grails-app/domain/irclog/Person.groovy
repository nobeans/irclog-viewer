package irclog

class Person {

    String loginName
    String realName
    String password
    String repassword // for confirmation (not storing to database)
    boolean enabled   // user staus (wether an user can login)
    String nicks // commpa separated nicks
    String color // for coloring on screen

    static hasMany = [channels:Channel, roles:Role]

    static belongsTo = Role

    static transients = [ 'repassword',
        'admin', 'accountExpired', 'accountLocked', 'passwordExpired'] // required because there is isXxxx method which is recognized as properties wrongly

    static constraints = {
        loginName(blank:false, matches:"[a-zA-Z0-9_-]{3,}", unique:true, maxSize:100)
        realName(blank:false, unique:true, maxSize:100)
        password(blank:false, minSize:6, maxSize:100, validator:{ val, obj -> obj.repassword == val })
        nicks(matches:"[ 0-9a-zA-Z_-]+", validator:{ val, obj ->
            val == "" || val.split(/ +/).every{ it.startsWith(obj.loginName) }
        }, maxSize:200)
        color(matches:"#[0-9A-Fa-f]{3}|#[0-9A-Fa-f]{6}")
        enabled()
        roles(nullable:false, size:1..1) // TODO in case of the hasMany's field, it seems that nullable:true is default...
        channels()
    }

    static mapping = {
        roles(column:'roles_id', joinTable:'role_person')
        channels(column:'person_channels_id', joinTable:'person_channel')
    }

    boolean isAdmin() {
        return (roles?.find{it.name == "ROLE_ADMIN"}) != null
    }
    boolean isAccountExpired()  { false }
    boolean isAccountLocked()   { false }
    boolean isPasswordExpired() { false }

}
