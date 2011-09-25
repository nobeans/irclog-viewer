package irclog

class Role {

    String name = 'ROLE_'
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
