package irclog

class Role {

    final static USER = 'ROLE_USER'
    final static ADMIN = 'ROLE_ADMIN'

    String name

    static constraints = {
        name(blank:false, unique:true)
    }

    static mapping = {
    }

    String toString() {
        name
    }
}
