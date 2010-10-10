package irclog

class Role {

    String name = 'ROLE_'
    String description

    static hasMany = [persons: Person]

    static constraints = {
        name(blank:false, unique:true)
        description()
    }

    static mapping = {
        description(type:'text')
        persons(column:'persons_id', joinTable:'role_person')
    }

    String toString() {
        name
    }
}
