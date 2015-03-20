package irclog

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@EqualsAndHashCode(includes = "name")
class Channel {

    String name
    String description = ""
    Boolean isPrivate = false
    Boolean isArchived = false
    String secretKey = ""

    Channel() {
        // In case of assigning in beforeInsert, summary.channel will be null somehow.
        summary = new Summary()
    }

    static hasOne = [summary: Summary]
    static hasMany = [persons: Person, irclogs: Irclog]

    static constraints = {
        name blank: false, unique: true, maxSize: 100, matches: /^#[^\s]+$/
        description()
        isPrivate()
        isArchived()
        secretKey maxSize: 100, validator: { val, obj ->
            if (obj.isPrivate) {
                return val != ''
            } else {
                return val == ''
            }
        }
        summary()
    }

    static mapping = {
        description type: 'text'

        // no cascade to irclogs because they should live standalone without relation with channel.
        irclogs cascade: 'none'
    }

    def beforeDelete() {
        // to behave as ON DELETE SET NULL
        Irclog.executeUpdate("update Irclog irclog set irclog.channel = null where irclog.channel = ?", [this])
    }
}
