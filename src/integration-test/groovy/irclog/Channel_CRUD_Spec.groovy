package irclog

import grails.test.mixin.integration.Integration
import irclog.utils.DateUtils
import irclog.utils.DomainUtils
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

@Integration
@Transactional
class Channel_CRUD_Spec extends Specification {

    Channel ch1
    Person user1, user2, user3
    Irclog irclog1, irclog2, irclog3

    def setup() {
        ch1 = new Channel(name: "#ch1")
        setupPerson()
        setupIrclog()
    }

    def "When channel is created, Summary is created and saved automatically."() {
        when:
        ch1.save(flush: true)

        then:
        ch1.id != null

        when: "hasMany properties are null if not refresh"
        ch1.refresh()

        then: "default values"
        ch1.name == "#ch1"
        ch1.description == ""
        !ch1.isPrivate
        !ch1.isArchived
        ch1.secretKey == ""
        ch1.persons == [] as Set
        ch1.irclogs == [] as Set

        and: "Summary is created and saved"
        Summary.countByChannel(ch1) == 1
        ch1.summary instanceof Summary
        ch1.summary.lastUpdated instanceof Date
        ch1.summary.today == 0
        ch1.summary.yesterday == 0
        ch1.summary.twoDaysAgo == 0
        ch1.summary.threeDaysAgo == 0
        ch1.summary.fourDaysAgo == 0
        ch1.summary.fiveDaysAgo == 0
        ch1.summary.sixDaysAgo == 0
        ch1.summary.totalBeforeYesterday == 0
        ch1.summary.total == 0
        ch1.summary.latestIrclog == null
        ch1.summary.channel == ch1
    }

    def "When channel is deleted, all associations are also deleted automatically."() {
        given:
        ch1.addToPersons(user1).addToPersons(user2)
        Irclog.list().each { ch1.addToIrclogs(it) }
        ch1.summary.latestIrclog = ch1.irclogs.find()
        ch1.save(flush: true)

        and:
        assert Channel.count() == 1
        assert Summary.countByChannel(ch1) == 1
        assert Person.count() == 4
        assert Irclog.count() == 3

        when:
        ch1.delete(flush: true)

        then:
        Channel.count() == 0
        Summary.countByChannel(ch1) == 0

        and: "persons and irclogs are still there"
        Person.count() == 4
        Irclog.count() == 3
    }

    def "When channel is deleted by HQL, cascading doesn't work well."() {
        given:
        ch1.addToPersons(user1).addToPersons(user2)
        Irclog.list().each { ch1.addToIrclogs(it) }
        ch1.summary.latestIrclog = ch1.irclogs.find()
        ch1.save(flush: true)

        and:
        assert Channel.count() == 1
        assert Summary.countByChannel(ch1) == 1
        assert Person.count() == 4
        assert Irclog.count() == 3

        when:
        Channel.executeUpdate("delete from Channel ch where ch.id = ?", [ch1.id])

        then:
        thrown DataIntegrityViolationException
    }

    //----------------------------------------------------
    // Helper methods

    private setupIrclog() {
        [1, 2, 3].each { id ->
            this."irclog${id}" = DomainUtils.createIrclog(
                permaId: "permaId:${id}",
                channelName: "DEFAULT",
                time: DateUtils.toDate("2010-01-01 00:00:0${id}"),
                type: "PRIVMSG",
            ).save(failOnError: true, validate: false)
        }
    }

    private setupPerson() {
        def roleUser = Role.findByName("ROLE_USER")
        [1, 2, 3].each { id ->
            this."user${id}" = DomainUtils.createPerson(
                loginName: "user${id}",
                role: roleUser,
            ).save(failOnError: true, validate: false)
        }
    }
}
