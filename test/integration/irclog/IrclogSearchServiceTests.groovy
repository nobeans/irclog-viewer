package irclog

import grails.test.*
import static irclog.utils.DomainUtils.*
import static irclog.utils.ConvertUtils.*

class IrclogSearchServiceTests extends GrailsUnitTestCase {

    def irclogSearchService
    def ch1, ch2, ch3
    def user1, user2, user3, admin

    void setUp() {
        setUpChannel()
        setUpPerson()
        setUpRelationBetweenPersonAndChannel()
    }

    void tearDown() {
        irclogSearchService.timeProvider.today = null
        super.tearDown()
    }

    void testSearch_period_all() {
        // Setup
        irclogSearchService.timeProvider.today = toDate("2011-01-01 00:00:01")
        def expected = []
        expected << saveIrclog(ch2, "2010-12-31 00:00:01", "user1", "PRIVMSG")
        expected << saveIrclog(ch2, "2009-12-31 00:00:01", "user1", "PRIVMSG")
        expected << saveIrclog(ch2, "2008-12-31 00:00:01", "user1", "PRIVMSG")
        // Exercise
        def criterion = createCriterion(period:'all')
        def actual = irclogSearchService.search(user3, criterion, [sort:"time"], 'desc')
        // Verify
        assert actual.list == expected
        assert actual.totalCount == 3
    }

    void testSearch_period_year() {
        // Setup
        irclogSearchService.timeProvider.today = toDate("2011-01-01 00:00:01")
        def expected = []
        expected << saveIrclog(ch2, "2010-12-31 00:00:01", "user1", "PRIVMSG")
        expected << saveIrclog(ch2, "2010-01-01 00:00:01", "user1", "PRIVMSG")
        expected << saveIrclog(ch2, "2010-01-01 00:00:00", "user1", "PRIVMSG")
        saveIrclog(ch2, "2009-12-31 00:00:00", "user1", "PRIVMSG")
        // Exercise
        def criterion = createCriterion(period:'year')
        def actual = irclogSearchService.search(user3, criterion, [sort:"time"], 'desc')
        // Verify
        assert actual.list == expected
        assert actual.totalCount == 3
    }

    void testSearch_period_halfyear() {
        // Setup
        irclogSearchService.timeProvider.today = toDate("2011-01-01 00:00:01")
        def expected = []
        expected << saveIrclog(ch2, "2010-12-31 00:00:01", "user1", "PRIVMSG")
        expected << saveIrclog(ch2, "2010-07-01 00:00:00", "user1", "PRIVMSG")
        saveIrclog(ch2, "2010-06-30 00:00:00", "user1", "PRIVMSG")
        // Exercise
        def criterion = createCriterion(period:'halfyear')
        def actual = irclogSearchService.search(user3, criterion, [sort:"time"], 'desc')
        // Verify
        assert actual.list == expected
        assert actual.totalCount == 2
    }

    //        // 今日(すべて)
    //        def criterion = [
    //            period:  'today',
    //            channel: 'all',
    //            type:    'filtered',
    //            nick:    '',
    //            message: '',
    //        ]
    //        // 今日＆時刻指定有りの場合
    //        def criterion = [
    //            period:  'today',
    //            'period-today-time': '12:34',
    //            channel: 'all',
    //            type:    'filtered',
    //            nick:    '',
    //            message: '',
    //        ]
    //        def criterion = [
    //            period:  'ondeday',
    //            'period-oneday-date': '2010-10-05',
    //            channel: 'all',
    //            type:    'filtered',
    //            nick:    '',
    //            message: '',
    //        ]

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

    private saveIrclog(Channel ch, String dateStr, String nick, String type) {
        def permaId = "log:${ch.name}:${dateStr}:${nick}:${type}"
        return createIrclog(permaId:permaId, channelName:ch.name, channel:ch, time:toDate(dateStr), type:type, nick:nick).saveSurely()
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
