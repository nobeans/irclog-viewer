package irclog

import irclog.utils.DateUtils
import static irclog.utils.DomainUtils.*
import grails.test.*
import org.junit.*

class TopicServiceTests {

    def topicService
    def ch1, ch2, ch3
    def user1, user2, user3, userX, admin
    def originalGetToday

    @Before
    void setUp() {
        setUpChannel()
        setUpPerson()
        setUpRelationBetweenPersonAndChannel()

        originalGetToday = DateUtils.metaClass.static.getToday
        DateUtils.metaClass.static.getToday = {-> return DateUtils.toDate("2011-01-01 12:34:56") }
    }

    @After
    void tearDown() {
        DateUtils.metaClass.static.getToday = originalGetToday
    }

    private setUpChannel() {
        ch1 = createChannel(name:"#ch1", description:"ch1 is nice!").save(failOnError:true)
        ch2 = createChannel(name:"#ch2", description:"ch2 is nice!").save(failOnError:true)
        ch3 = createChannel(name:"#ch3", description:"ch3 is nice!").save(failOnError:true)
    }
    private setUpPerson() {
        admin = Person.findByLoginName("admin") // setup in Bootstrap
        def roleUser = Role.findByName("ROLE_USER")
        ["1", "2", "3", "X"].each { id ->
            def user = createPerson(loginName:"user${id}").save(failOnError:true)
            user.addToRoles(roleUser)
            this."user${id}" = user
        }
    }
    private setUpRelationBetweenPersonAndChannel() {
        // #ch1[user1], #ch2[user2], #ch3[user3, userX]
        user1.addToChannels(ch1)
        user2.addToChannels(ch2)
        user3.addToChannels(ch3)
        userX.addToChannels(ch3)
    }

    @Test
    void getHotTopicList_withinOneWeekAgo() {
        // Setup
        def expected = []
        expected << saveIrclog(time:"2011-01-01 12:34:56", type:"TOPIC")
        expected << saveIrclog(time:"2010-12-31 12:34:56", type:"TOPIC")
        expected << saveIrclog(time:"2010-12-30 12:34:56", type:"TOPIC")
        expected << saveIrclog(time:"2010-12-25 23:59:59", type:"TOPIC") // boundary
        saveIrclog(time:"2010-12-24 00:00:00", type:"TOPIC")
        saveIrclog(time:"2010-12-24 12:34:56", type:"TOPIC")
        // Exercise
        def topics = topicService.getHotTopicList(ch2)
        // Verify
        assert topics == expected
    }

    @Test
    void getHotTopicList_onlyTopic() {
        // Setup
        def expected = []
        expected << saveIrclog(time:"2011-01-01 12:34:56", type:"TOPIC")
        saveIrclog(time:"2011-01-01 12:34:56", type:"PRIVMSG")
        saveIrclog(time:"2011-01-01 12:34:56", type:"NOTICE")
        Irclog.OPTION_TYPES.each { type ->
            saveIrclog(time:"2011-01-01 12:34:56", type:type)
        }
        // Exercise
        def topics = topicService.getHotTopicList(ch2)
        // Verify
        assert topics == expected
    }

    @Test
    void getHotTopicList_onlyInAccessibleChannels() {
        // Setup
        def expected = []
        saveIrclog(time:"2011-01-01 12:34:56", type:"TOPIC", channel:ch1)
        expected << saveIrclog(time:"2011-01-01 12:34:56", type:"TOPIC", channel:ch2)
        expected << saveIrclog(time:"2011-01-01 12:34:56", type:"TOPIC", channel:ch3)
        Irclog.OPTION_TYPES.each { type ->
            saveIrclog(time:"2011-01-01 12:34:56", type:type)
        }
        // Exercise
        def topics = topicService.getHotTopicList([ch2, ch3])
        // Verify
        assert topics == expected
    }

    @Test
    void getHotTopicList_notExistsAnyAccessibleChannels() {
        // Setup
        saveIrclog(time:"2011-01-01 12:34:56", type:"TOPIC", channel:ch1)
        saveIrclog(time:"2011-01-01 12:34:56", type:"TOPIC", channel:ch2)
        saveIrclog(time:"2011-01-01 12:34:56", type:"TOPIC", channel:ch3)
        // Exercise
        def topics = topicService.getHotTopicList([])
        // Verify
        assert topics == []
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
        return createIrclog(mergedMap).save(failOnError:true)
    }
}
