package irclog

import grails.test.mixin.integration.Integration
import irclog.utils.DateUtils
import irclog.utils.DomainUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

@Integration
@Transactional
class TopicServiceSpec extends Specification {

    @Autowired
    TopicService topicService

    def ch1, ch2, ch3
    def user1, user2, user3, userX, admin

    def setup() {
        setupChannel()
        setupPerson()
        setupRelationBetweenPersonAndChannel()

        topicService.getToday = { DateUtils.toDate("2011-01-01 12:34:56") }
    }

    def "getHotTopicList() should return topic list which are after a week ago"() {
        given:
        def expected = []
        expected << saveIrclog(time: "2011-01-01 12:34:56", type: "TOPIC")
        expected << saveIrclog(time: "2010-12-31 12:34:56", type: "TOPIC")
        expected << saveIrclog(time: "2010-12-30 12:34:56", type: "TOPIC")
        expected << saveIrclog(time: "2010-12-25 23:59:59", type: "TOPIC")
        saveIrclog(time: "2010-12-24 00:00:00", type: "TOPIC")
        saveIrclog(time: "2010-12-24 12:34:56", type: "TOPIC")

        when:
        def topics = topicService.getHotTopicList(ch2)

        then:
        topics == expected
    }

    def "getHotTopicList() should return topic list which are only TOPIC type"() {
        given:
        def expected = []
        expected << saveIrclog(time: "2011-01-01 12:34:56", type: "TOPIC")
        saveIrclog(time: "2011-01-01 12:34:55", type: "PRIVMSG")
        saveIrclog(time: "2011-01-01 12:34:54", type: "NOTICE")
        Irclog.OPTION_TYPES.each { type ->
            saveIrclog(time: "2011-01-01 12:34:53", type: type)
        }

        when:
        def topics = topicService.getHotTopicList(ch2)

        then:
        topics == expected
    }

    def "getHotTopicList() should return topic list which are only in accessible channels"() {
        given:
        def expected = []
        saveIrclog(time: "2011-01-01 12:34:56", type: "TOPIC", channel: ch1)
        expected << saveIrclog(time: "2011-01-01 12:34:55", type: "TOPIC", channel: ch2)
        expected << saveIrclog(time: "2011-01-01 12:34:54", type: "TOPIC", channel: ch3)
        Irclog.OPTION_TYPES.each { type ->
            saveIrclog(time: "2011-01-01 12:34:53", type: type)
        }

        when:
        def topics = topicService.getHotTopicList([ch2, ch3])

        then:
        topics == expected
    }

    def "getHotTopicList() should return empty list when there no topic in accessible channels"() {
        given:
        saveIrclog(time: "2011-01-01 12:34:56", type: "TOPIC", channel: ch1)
        saveIrclog(time: "2011-01-01 12:34:55", type: "TOPIC", channel: ch2)
        saveIrclog(time: "2011-01-01 12:34:54", type: "TOPIC", channel: ch3)

        when:
        def topics = topicService.getHotTopicList([])

        then:
        topics == []
    }

    //--------------------------------------
    // Helper methods

    private setupChannel() {
        ch1 = DomainUtils.createChannel(name: "#ch1", description: "ch1 is nice!").save(failOnError: true)
        ch2 = DomainUtils.createChannel(name: "#ch2", description: "ch2 is nice!").save(failOnError: true)
        ch3 = DomainUtils.createChannel(name: "#ch3", description: "ch3 is nice!").save(failOnError: true)
    }

    private setupPerson() {
        admin = Person.findByLoginName("admin") // setup in Bootstrap
        def roleUser = Role.findByName("ROLE_USER")
        ["1", "2", "3", "X"].each { id ->
            def user = DomainUtils.createPerson(
                loginName: "user${id}",
                role: roleUser,
            ).save(failOnError: true)
            this."user${id}" = user
        }
    }

    private setupRelationBetweenPersonAndChannel() {
        // #ch1[user1], #ch2[user2], #ch3[user3, userX]
        ch1.addToPersons(user1)
        ch2.addToPersons(user2)
        ch3.addToPersons(user3).addToPersons(userX)
    }

    private saveIrclog(propMap = [:]) {
        if (propMap.time in String) {
            propMap.time = DateUtils.toDate(propMap.time)
        }
        if (propMap.channel) {
            propMap.channelName = propMap.channel.name
        }
        def defaultMap = [
            channel: ch2,
            channelName: ch2.name,
            time: new Date(),
            nick: "user1",
            type: "PRIVMSG",
        ]
        def mergedMap = defaultMap + propMap
        return DomainUtils.createIrclog(mergedMap).save(failOnError: true)
    }
}
