package irclog

import groovy.transform.ToString

@ToString
class Channel {

    String name
    String description
    Boolean isPrivate
    Boolean isArchived
    String secretKey

    static constraints = {
        name(blank: false, unique: true, maxSize: 100, matches: "^#.*")
        description()
        isPrivate()
        isArchived()
        secretKey(maxSize: 100, validator: { val, obj ->
            if (obj.isPrivate) {
                return val != ''
            } else {
                return val == ''
            }
        })
    }

    static mapping = { description(type: 'text') }

    @Override
    boolean equals(obj) {
        if (!(obj instanceof Channel)) return false
        return (obj.name == this.name)
    }

    @Override
    int hashCode() {
        return (this.name ?: "?").hashCode() * 17
    }
}
