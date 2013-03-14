package irclog

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

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

    Summary getSummary() {
        Summary.findByChannel(this)
    }

    def saveWithSummary(options = [:]) {
        def saved = this.save(options)
        if (saved && !summary) {
            new Summary(channel: this).save(failOnError: true) // should not be failed
        }
        saved
    }
}
