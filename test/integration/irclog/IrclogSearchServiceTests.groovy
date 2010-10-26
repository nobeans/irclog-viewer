package irclog

import grails.test.*
import irclog.utils.DateUtils
import static irclog.utils.DomainUtils.*

class IrclogSearchServiceTests extends GrailsUnitTestCase {

    def irclogSearchService
    def ch1, ch2, ch3
    def user1, user2, user3, admin
    def today

    void setUp() {
        super.setUp()
        
        setUpChannel()
        setUpPerson()
        setUpRelationBetweenPersonAndChannel()
        
        today = DateUtils.toDate("2011-01-01 12:34:56")
        mockFor(DateUtils).demand.static.getToday(1..100) {-> return today }
    }

    void testSearch_period_all() {
        // Setup
        def expected = []
        expected << saveIrclog(time:"2010-12-31 12:34:56")
        expected << saveIrclog(time:"2009-12-31 12:34:56")
        expected << saveIrclog(time:"2008-12-31 12:34:56")
        // Exercise
        def criterion = createCriterion(period:'all')
        def actual = irclogSearchService.search(user3, criterion, [sort:"time"], 'desc')
        // Verify
        assert actual.list == expected
        assert actual.totalCount == expected.size()
    }

    void testSearch_period_year() {
        // Setup
        def expected = []
        expected << saveIrclog(time:"2010-12-31 12:34:56")
        expected << saveIrclog(time:"2010-01-01 12:34:56")
        expected << saveIrclog(time:"2010-01-01 00:00:00")
        saveIrclog(time:"2009-12-31 23:59:59")
        // Exercise
        def criterion = createCriterion(period:'year')
        def actual = irclogSearchService.search(user3, criterion, [sort:"time"], 'desc')
        // Verify
        assert actual.list == expected
        assert actual.totalCount == expected.size()
    }

    void testSearch_period_halfyear() {
        // Setup
        def expected = []
        expected << saveIrclog(time:"2010-12-31 12:34:56")
        expected << saveIrclog(time:"2010-07-01 00:00:00")
        saveIrclog(time:"2010-06-30 23:59:59")
        // Exercise
        def criterion = createCriterion(period:'halfyear')
        def actual = irclogSearchService.search(user3, criterion, [sort:"time"], 'desc')
        // Verify
        assert actual.list == expected
        assert actual.totalCount == expected.size()
    }

    void testSearch_period_month() {
        // Setup
        def expected = []
        expected << saveIrclog(time:"2010-12-01 12:34:56")
        expected << saveIrclog(time:"2010-12-01 00:00:00")
        saveIrclog(time:"2010-11-30 23:59:59")
        // Exercise
        def criterion = createCriterion(period:'month')
        def actual = irclogSearchService.search(user3, criterion, [sort:"time"], 'desc')
        // Verify
        assert actual.list == expected
        assert actual.totalCount == expected.size()
    }

    void testSearch_period_week() {
        // Setup
        def expected = []
        expected << saveIrclog(time:"2010-12-31 12:34:56")
        expected << saveIrclog(time:"2010-12-25 00:00:01")
        expected << saveIrclog(time:"2010-12-25 00:00:00")
        saveIrclog(time:"2010-12-24 23:59:59")
        // Exercise
        def criterion = createCriterion(period:'week')
        def actual = irclogSearchService.search(user3, criterion, [sort:"time"], 'desc')
        // Verify
        assert actual.list == expected
        assert actual.totalCount == expected.size()
    }

    void testSearch_period_oneday() {
        // Setup
        def expected = []
        saveIrclog(time:"2010-12-31 00:00:00")
        expected << saveIrclog(time:"2010-12-24 23:59:59")
        expected << saveIrclog(time:"2010-12-24 12:34:56")
        expected << saveIrclog(time:"2010-12-24 00:00:00")
        saveIrclog(time:"2010-12-23 23:59:59")
        // Exercise
        def criterion = createCriterion(period:'oneday', 'period-oneday-date':'2010-12-24')
        def actual = irclogSearchService.search(user3, criterion, [sort:"time"], 'desc')
        // Verify
        assert actual.list == expected
        assert actual.totalCount == expected.size()
    }

    void testSearch_period_today() {
        // Setup
        def expected = []
        expected << saveIrclog(time:"2011-01-01 23:59:59")
        expected << saveIrclog(time:"2011-01-01 12:34:56")
        expected << saveIrclog(time:"2011-01-01 00:00:00")
        saveIrclog(time:"2010-12-31 23:59:59")
        // Exercise
        def criterion = createCriterion(period:'today')
        def actual = irclogSearchService.search(user3, criterion, [sort:"time"], 'desc')
        // Verify
        assert actual.list == expected
        assert actual.totalCount == expected.size()
    }

    private setUpChannel() {
        (1..3).each { num ->
            this."ch${num}" = createChannel(name:"#ch${num}", description:"${10 - num}").saveSurely()
        }
    }

    private setUpPerson() {
        admin = Person.findByLoginName("admin") // setup in Bootstrap
        def roleUser = Role.findByName("ROLE_USER")
        ["1", "2", "3"].each { id ->
            def user = createPerson(loginName:"user${id}").saveSurely()
            user.addToRoles(roleUser)
            this."user${id}" = user
        }
    }

    private setUpRelationBetweenPersonAndChannel() {
        // #ch1[user1, user2, user3], #ch2[user2, user3], #ch3[user3]
        user1.addToChannels(ch1)
        user2.addToChannels(ch1)
        user2.addToChannels(ch2)
        user3.addToChannels(ch1)
        user3.addToChannels(ch2)
        user3.addToChannels(ch3)
    }

    private saveIrclog(propMap) {
        if (propMap.time in String) {
            propMap.time = DateUtils.toDate(propMap.time)
        }
        if (propMap.channel) {
            propMap.channelName = propMap.channel.name
        }
        def defaultMap = [
            channel: ch2,
            channelName: ch2.name,
            time: today,
            nick: "user1",
            type: "PRIVMSG",
        ]
        def mergedMap = defaultMap + propMap
        mergedMap.permaId = "log:${mergedMap.ch}:${mergedMap.time}:${mergedMap.nick}:${mergedMap.type}"
        return createIrclog(mergedMap).saveSurely()
    }

    private createCriterion(map) {
        return [
            period:  'all',
            channel: 'all',
            type:    'all',
            nick:    '',
            message: '',
        ] + map
    }
}
