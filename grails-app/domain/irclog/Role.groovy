package irclog

class Role {

    final static USER = 'ROLE_USER'
    final static ADMIN = 'ROLE_ADMIN'

    String name
    String description

    static constraints = {
        name(blank:false, unique:true)
        description()
    }

    static mapping = {
        description(type:'text')
    }

    String toString() {
        name
    }
}
