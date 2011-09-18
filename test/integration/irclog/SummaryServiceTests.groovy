package irclog

import irclog.utils.DateUtils
import static irclog.utils.DomainUtils.*
import grails.test.*
import org.junit.*

class SummaryServiceTests {

    def summaryService
    def ch1, ch2, ch3
    def user1, user2, user3, userX, admin
    def originalGetToday

    @Before
    void setUp() {
        setUpChannel()
        setUpPerson()
        setUpRelationBetweenPersonAndChannel()
        setUpIrclog()
        setUpSummary()

        originalGetToday = DateUtils.metaClass.static.getToday
        DateUtils.metaClass.static.getToday = {-> return DateUtils.toDate("2011-01-01 12:34:56") }
    }

    @After
    void tearDown() {
        DateUtils.metaClass.static.getToday = originalGetToday
    }

    private setUpChannel() {
        (1..3).each { num ->
            this."ch${num}" = saveSurely(createChannel(name:"#ch${num}", description:"${10 - num}"))
        }
    }
    private setUpPerson() {
        admin = Person.findByLoginName("admin") // setup in Bootstrap
        def roleUser = Role.findByName("ROLE_USER")
        ["1", "2", "3", "X"].each { id ->
            def user = saveSurely(createPerson(loginName:"user${id}"))
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
    private setUpIrclog() {
//        saveIrclog(channel:ch1, type:"TOPIC", message:"It's important topic")
//        saveIrclog(channel:ch1, type:"PRIVMSG", message:"It's just a private message")
//        saveIrclog(channel:ch2, type:"TOPIC", message:"It's important topic")
//        saveIrclog(channel:ch2, type:"JOIN", message:"It's just a join log")
//        saveIrclog(channel:ch3, type:"TOPIC", message:"It's important topic")
    }
    private setUpSummary() {
//        saveSurely(createSummary(channel:ch1, latestIrclog:Irclog.findByPermaId("log:ch1:0")))
//        saveSurely(createSummary(channel:ch2, latestIrclog:Irclog.findByPermaId("log:ch2:0")))
//        saveSurely(createSummary(channel:ch3, latestIrclog:Irclog.findByPermaId("log:ch3:0")))
    }

    void testGetHotTopicList_withinOneWeekAgo() {
        // Setup
        def expected = []
        expected << saveIrclog(time:"2011-01-01 12:34:56", type:"TOPIC")
        expected << saveIrclog(time:"2010-12-31 12:34:56", type:"TOPIC")
        expected << saveIrclog(time:"2010-12-30 12:34:56", type:"TOPIC")
        expected << saveIrclog(time:"2010-12-25 23:59:59", type:"TOPIC") // boundary
        saveIrclog(time:"2010-12-24 00:00:00", type:"TOPIC")
        saveIrclog(time:"2010-12-24 12:34:56", type:"TOPIC")
        // Exercise
        def topics = summaryService.getHotTopicList(ch2)
        // Verify
        assert topics == expected
    }

    void testGetHotTopicList_onlyTopic() {
        // Setup
        def expected = []
        expected << saveIrclog(time:"2011-01-01 12:34:56", type:"TOPIC")
        saveIrclog(time:"2011-01-01 12:34:56", type:"PRIVMSG")
        saveIrclog(time:"2011-01-01 12:34:56", type:"NOTICE")
        Irclog.OPTION_TYPES.each { type ->
            saveIrclog(time:"2011-01-01 12:34:56", type:type)
        }
        // Exercise
        def topics = summaryService.getHotTopicList(ch2)
        // Verify
        assert topics == expected
    }

    void testGetHotTopicList_onlyInAccessibleChannels() {
        // Setup
        def expected = []
        saveIrclog(time:"2011-01-01 12:34:56", type:"TOPIC", channel:ch1)
        expected << saveIrclog(time:"2011-01-01 12:34:56", type:"TOPIC", channel:ch2)
        expected << saveIrclog(time:"2011-01-01 12:34:56", type:"TOPIC", channel:ch3)
        Irclog.OPTION_TYPES.each { type ->
            saveIrclog(time:"2011-01-01 12:34:56", type:type)
        }
        // Exercise
        def topics = summaryService.getHotTopicList([ch2, ch3])
        // Verify
        assert topics == expected
    }

    void testGetHotTopicList_notExistsAnyAccessibleChannels() {
        // Setup
        saveIrclog(time:"2011-01-01 12:34:56", type:"TOPIC", channel:ch1)
        saveIrclog(time:"2011-01-01 12:34:56", type:"TOPIC", channel:ch2)
        saveIrclog(time:"2011-01-01 12:34:56", type:"TOPIC", channel:ch3)
        // Exercise
        def topics = summaryService.getHotTopicList([])
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
        def permaId =  mergedMap.toString() // to avoid stack overflow
        mergedMap.permaId = permaId
        return saveSurely(createIrclog(mergedMap))
    }
}
