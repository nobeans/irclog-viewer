package irclog

import groovy.transform.ToString
import groovy.transform.EqualsAndHashCode

@ToString
@EqualsAndHashCode(includes = "name")
class Channel {

    String name
    String description
    Boolean isPrivate
    Boolean isArchived
    String secretKey

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
    }

    static mapping = {
        description type: 'text'
    }

    def afterInsert() {
        // Summary is necessary for Channel.
        Summary.withNewSession {
            new Summary(channel: this).save(failOnError: true)
        }
    }
}
