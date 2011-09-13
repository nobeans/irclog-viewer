package irclog

import irclog.utils.DateUtils
import static irclog.utils.DomainUtils.*
import grails.test.mixin.*
import org.junit.*

@TestFor(IrclogSearchService)
class IrclogSearchServiceTests {

    def irclogSearchService
    def ch1, ch2, ch3
    def user1, user2, user3, userX, admin

    @Before
    void setUp() {
        setUpChannel()
        setUpPerson()
        setUpRelationBetweenPersonAndChannel()
    }

    void testSearch_period_all() {
        // Setup
        setUpTodayOfDateUtils("2011-01-01 12:34:56")
        def expected = []
        expected << saveIrclog(time:"2010-12-31 12:34:56")
        expected << saveIrclog(time:"2009-12-31 12:34:56")
        expected << saveIrclog(time:"2008-12-31 12:34:56")
        // Exercise
        def actual = irclogSearchService.search(user3, createCriterion(period:'all'), [:], 'desc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_period_year() {
        // Setup
        setUpTodayOfDateUtils("2011-01-01 12:34:56")
        def expected = []
        expected << saveIrclog(time:"2010-12-31 12:34:56")
        expected << saveIrclog(time:"2010-01-01 12:34:56")
        expected << saveIrclog(time:"2010-01-01 00:00:00")
        saveIrclog(time:"2009-12-31 23:59:59")
        // Exercise
        def actual = irclogSearchService.search(user3, createCriterion(period:'year'), [:], 'desc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_period_halfyear() {
        // Setup
        setUpTodayOfDateUtils("2011-01-01 12:34:56")
        def expected = []
        expected << saveIrclog(time:"2010-12-31 12:34:56")
        expected << saveIrclog(time:"2010-07-01 00:00:00")
        saveIrclog(time:"2010-06-30 23:59:59")
        // Exercise
        def actual = irclogSearchService.search(user3, createCriterion(period:'halfyear'), [:], 'desc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_period_month() {
        // Setup
        setUpTodayOfDateUtils("2011-01-01 12:34:56")
        def expected = []
        expected << saveIrclog(time:"2010-12-01 12:34:56")
        expected << saveIrclog(time:"2010-12-01 00:00:00")
        saveIrclog(time:"2010-11-30 23:59:59")
        // Exercise
        def actual = irclogSearchService.search(user3, createCriterion(period:'month'), [:], 'desc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_period_week() {
        // Setup
        setUpTodayOfDateUtils("2011-01-01 12:34:56")
        def expected = []
        expected << saveIrclog(time:"2010-12-31 12:34:56")
        expected << saveIrclog(time:"2010-12-25 00:00:01")
        expected << saveIrclog(time:"2010-12-25 00:00:00")
        saveIrclog(time:"2010-12-24 23:59:59")
        // Exercise
        def actual = irclogSearchService.search(user3, createCriterion(period:'week'), [:], 'desc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_period_oneday() {
        // Setup
        setUpTodayOfDateUtils("2011-01-01 12:34:56")
        def expected = []
        saveIrclog(time:"2010-12-31 00:00:00")
        expected << saveIrclog(time:"2010-12-24 23:59:59")
        expected << saveIrclog(time:"2010-12-24 12:34:56")
        expected << saveIrclog(time:"2010-12-24 00:00:00")
        saveIrclog(time:"2010-12-23 23:59:59")
        // Exercise
        def criterion = createCriterion(period:'oneday', 'period-oneday-date':'2010-12-24')
        def actual = irclogSearchService.search(user3, criterion, [:], 'desc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_period_today() {
        // Setup
        setUpTodayOfDateUtils("2011-01-01 12:34:56")
        def expected = []
        expected << saveIrclog(time:"2011-01-01 23:59:59")
        expected << saveIrclog(time:"2011-01-01 12:34:56")
        expected << saveIrclog(time:"2011-01-01 00:00:00")
        saveIrclog(time:"2010-12-31 23:59:59")
        // Exercise
        def actual = irclogSearchService.search(user3, createCriterion(period:'today'), [:], 'desc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_period_nullShouldBeError() {
        shouldFail(AssertionError) {
            irclogSearchService.search(admin, createCriterion(period:null), [:], 'asc')
        }
    }

    void testSearch_period_emptyShouldBeError() {
        shouldFail(AssertionError) {
            irclogSearchService.search(admin, createCriterion(period:''), [:], 'asc')
        }
    }

    void testSearch_channel_all_generalUser_joinedToSomeChannels() {
        // Setup
        def expected = []
        expected << saveIrclog(channel:ch1)
        expected << saveIrclog(channel:ch2)
        saveIrclog(channel:ch3)
        // Exercise
        def actual = irclogSearchService.search(user2, createCriterion(channel:'all'), [:], 'asc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_channel_all_generalUser_notJoinedToAnyChannels() {
        // Setup
        def expected = []
        saveIrclog(channel:ch1)
        saveIrclog(channel:ch2)
        saveIrclog(channel:ch3)
        // Exercise
        def actual = irclogSearchService.search(userX, createCriterion(channel:'all'), [:], 'asc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_channel_all_admin() {
        // Setup
        def expected = []
        expected << saveIrclog(channel:ch1)
        expected << saveIrclog(channel:ch2)
        expected << saveIrclog(channel:ch3)
        // Exercise
        def actual = irclogSearchService.search(admin, createCriterion(channel:'all'), [:], 'asc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_channel_specifiedChannel_generalUser_joinedToSomeChannels() {
        // Setup
        def expected = []
        saveIrclog(channel:ch1)
        expected << saveIrclog(channel:ch2)
        saveIrclog(channel:ch3)
        // Exercise
        def actual = irclogSearchService.search(user2, createCriterion(channel:'#ch2'), [:], 'asc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_channel_specifiedChannel_generalUser_notJoinedToAnyChannels() {
        // Setup
        def expected = []
        saveIrclog(channel:ch1)
        saveIrclog(channel:ch2)
        saveIrclog(channel:ch3)
        // Exercise
        def actual = irclogSearchService.search(userX, createCriterion(channel:'#ch2'), [:], 'asc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_channel_specifiedChannel_admin() {
        // Setup
        def expected = []
        saveIrclog(channel:ch1)
        expected << saveIrclog(channel:ch2)
        saveIrclog(channel:ch3)
        // Exercise
        def actual = irclogSearchService.search(admin, createCriterion(channel:'#ch2'), [:], 'asc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_channel_nullShouldBeError() {
        shouldFail(AssertionError) {
            irclogSearchService.search(admin, createCriterion(channel:null), [:], 'asc')
        }
    }

    void testSearch_channel_emptyShouldBeError() {
        shouldFail(AssertionError) {
            irclogSearchService.search(admin, createCriterion(channel:''), [:], 'asc')
        }
    }

    void testSearch_type_all() {
        // Setup
        def expected = []
        Irclog.ESSENTIAL_TYPES.each{ expected << saveIrclog(type:it) }
        Irclog.OPTION_TYPES.each{ expected << saveIrclog(type:it) }
        // Exercise
        def actual = irclogSearchService.search(admin, createCriterion(type:'all'), [:], 'asc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_type_filtered() {
        // Setup
        def expected = []
        Irclog.ESSENTIAL_TYPES.each{ expected << saveIrclog(type:it) }
        Irclog.OPTION_TYPES.each{ saveIrclog(type:it) }
        // Exercise
        def actual = irclogSearchService.search(admin, createCriterion(type:'filtered'), [:], 'asc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_type_specifiedOneType_JOIN() {
        // Setup
        def expected = []
        Irclog.ESSENTIAL_TYPES.each{ saveIrclog(type:it) }
        (Irclog.OPTION_TYPES - "JOIN").each{ saveIrclog(type:it) }
        expected << saveIrclog(type:"JOIN")
        // Exercise
        def actual = irclogSearchService.search(admin, createCriterion(type:'JOIN'), [:], 'asc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_type_nullShouldBeError() {
        shouldFail(AssertionError) {
            irclogSearchService.search(admin, createCriterion(type:null), [:], 'asc')
        }
    }

    void testSearch_type_emptyShouldBeError() {
        shouldFail(AssertionError) {
            irclogSearchService.search(admin, createCriterion(type:''), [:], 'asc')
        }
    }

    void testSearch_nick_matchToPartOfNick() {
        // Setup
        def expected = []
        expected << saveIrclog(nick:"john")
        expected << saveIrclog(nick:"jojo")
        saveIrclog(nick:"mike")
        // Exercise
        def actual = irclogSearchService.search(admin, createCriterion(nick:'O'), [:], 'asc') // ignored case
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_nick_matchToNothing() {
        // Setup
        def expected = []
        saveIrclog(nick:"john")
        saveIrclog(nick:"jojo")
        saveIrclog(nick:"mike")
        // Exercise
        def actual = irclogSearchService.search(admin, createCriterion(nick:'nancy'), [:], 'asc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_nick_null_meansAll() {
        // Setup
        def expected = []
        expected << saveIrclog(nick:"john")
        expected << saveIrclog(nick:"jojo")
        expected << saveIrclog(nick:"mike")
        // Exercise
        def actual = irclogSearchService.search(admin, createCriterion(nick:null), [:], 'asc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_nick_empty_meansAll() {
        // Setup
        def expected = []
        expected << saveIrclog(nick:"john")
        expected << saveIrclog(nick:"jojo")
        expected << saveIrclog(nick:"mike")
        // Exercise
        def actual = irclogSearchService.search(admin, createCriterion(nick:''), [:], 'asc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_message_matchToPartOfNick() {
        // Setup
        def expected = []
        expected << saveIrclog(message:"Hello, john")
        expected << saveIrclog(message:"Hello, jojo")
        saveIrclog(message:"Hello, mike")
        // Exercise
        def actual = irclogSearchService.search(admin, createCriterion(message:'jO'), [:], 'asc') // ignored case
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_message_matchToNothing() {
        // Setup
        def expected = []
        saveIrclog(message:"Hello, john")
        saveIrclog(message:"Hello, jojo")
        saveIrclog(message:"Hello, mike")
        // Exercise
        def actual = irclogSearchService.search(admin, createCriterion(message:'nancy'), [:], 'asc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_message_null_meansAll() {
        // Setup
        def expected = []
        expected << saveIrclog(message:"Hello, john")
        expected << saveIrclog(message:"Hello, jojo")
        expected << saveIrclog(message:"Hello, mike")
        // Exercise
        def actual = irclogSearchService.search(admin, createCriterion(message:null), [:], 'asc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_message_empty_meansAll() {
        // Setup
        def expected = []
        expected << saveIrclog(message:"Hello, john")
        expected << saveIrclog(message:"Hello, jojo")
        expected << saveIrclog(message:"Hello, mike")
        // Exercise
        def actual = irclogSearchService.search(admin, createCriterion(message:''), [:], 'asc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_sortDirection_desc() {
        // Setup
        def expected = []
        expected << saveIrclog(time:"2010-01-01 00:00:03")
        expected << saveIrclog(time:"2010-01-01 00:00:02")
        expected << saveIrclog(time:"2010-01-01 00:00:01")
        // Exercise
        def actual = irclogSearchService.search(user3, createCriterion(), [:], 'desc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_sortDirection_asc() {
        // Setup
        def expected = []
        expected << saveIrclog(time:"2010-01-01 00:00:01")
        expected << saveIrclog(time:"2010-01-01 00:00:02")
        expected << saveIrclog(time:"2010-01-01 00:00:03")
        // Exercise
        def actual = irclogSearchService.search(user3, createCriterion(), [:], 'asc')
        // Verify
        assertSearchResult(expected, actual)
    }

    void testSearch_max() {
        // Setup
        def expected = []
        expected << saveIrclog(time:"2010-01-01 00:00:01")
        expected << saveIrclog(time:"2010-01-01 00:00:02")
        saveIrclog(time:"2010-01-01 00:00:03")
        // Exercise
        def actual = irclogSearchService.search(user3, createCriterion(), [max:2], 'asc')
        // Verify
        assert actual.list == expected
        assert actual.totalCount == 3
    }

    void testSearch_offset() {
        // Setup
        def expected = []
        saveIrclog(time:"2010-01-01 00:00:01")
        expected << saveIrclog(time:"2010-01-01 00:00:02")
        expected << saveIrclog(time:"2010-01-01 00:00:03")
        // Exercise
        def actual = irclogSearchService.search(user3, createCriterion(), [offset:1], 'asc')
        // Verify
        assert actual.list == expected
        assert actual.totalCount == 3
    }

    void testSearch_offsetAndMax() {
        // Setup
        def expected = []
        saveIrclog(time:"2010-01-01 00:00:01")
        expected << saveIrclog(time:"2010-01-01 00:00:02")
        saveIrclog(time:"2010-01-01 00:00:03")
        // Exercise
        def actual = irclogSearchService.search(user3, createCriterion(), [offset:1, max:1], 'asc')
        // Verify
        assert actual.list == expected
        assert actual.totalCount == 3
    }

    // -------------------------------------
    // Test helpers

    private setUpChannel() {
        (1..3).each { num ->
            this."ch${num}" = createChannel(name:"#ch${num}", description:"${10 - num}").saveSurely()
        }
    }

    private setUpPerson() {
        admin = Person.findByLoginName("admin") // setup in Bootstrap
        def roleUser = Role.findByName("ROLE_USER")
        ["1", "2", "3", "X"].each { id ->
            def user = createPerson(loginName:"user${id}").saveSurely()
            user.addToRoles(roleUser)
            this."user${id}" = user
        }
    }

    private setUpRelationBetweenPersonAndChannel() {
        // #ch1[user1, user2, user3], #ch2[user2, user3], #ch3[user3]
        // userX isn't allowed to access any channel.
        user1.addToChannels(ch1)
        user2.addToChannels(ch1)
        user2.addToChannels(ch2)
        user3.addToChannels(ch1)
        user3.addToChannels(ch2)
        user3.addToChannels(ch3)
    }

    private setUpTodayOfDateUtils(dateStr) {
        mockFor(DateUtils).demand.static.getToday(1..100) {-> return DateUtils.toDate(dateStr) }
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
        return createIrclog(mergedMap).saveSurely()
    }

    private createCriterion(map = [:]) {
        return [
            period:  'all',
            channel: 'all',
            type:    'all',
            nick:    '',
            message: '',
        ] + map
    }

    private assertSearchResult(expected, actual) {
        assert actual.list == expected
        assert actual.totalCount == expected.size()
    }
}
