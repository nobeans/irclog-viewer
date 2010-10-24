package irclog

import grails.test.*
import static irclog.utils.DomainUtils.*
import static irclog.utils.ConvertUtils.*

class ChannelServiceTests extends GrailsUnitTestCase {

    def channelService
    def ch1, ch2, ch3
    def user1, user2, user3, userX, admin

    protected void setUp() {
        super.setUp()
        setUpChannel()
        setUpPerson()
        setUpRelationBetweenPersonAndChannel()
        setUpIrclog()
        setUpSummary()
    }

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
        // #ch1[user1], #ch2[user2], #ch3[user3, userX]
        user1.addToChannels(ch1)
        user2.addToChannels(ch2)
        user3.addToChannels(ch3)
        userX.addToChannels(ch3)
    }
    private setUpIrclog() {
        2.times { id ->
            createIrclog(permaId:"log:ch1:${id}", channelName:ch1.name, time:toDate("2010-01-01"), channel:null).saveSurely()
            createIrclog(permaId:"log:ch2:${id}", channelName:ch2.name, time:toDate("2010-01-01"), channel:ch2).saveSurely()
            createIrclog(permaId:"log:ch3:${id}", channelName:ch3.name, time:toDate("2010-01-01"), channel:ch3).saveSurely()
        }
    }
    private setUpSummary() {
        createSummary(channel:ch1, latestIrclog:Irclog.findByPermaId("log:ch1:0")).saveSurely()
        createSummary(channel:ch2, latestIrclog:Irclog.findByPermaId("log:ch2:0")).saveSurely()
        createSummary(channel:ch3, latestIrclog:Irclog.findByPermaId("log:ch3:0")).saveSurely()
    }

    void testGetAccessibleChannelList_admin() {
        // Exercise
        def channels = channelService.getAccessibleChannelList(admin, [:])
        // Verify
        assert channels == [ch1, ch2, ch3]
    }

    void testGetAccessibleChannelList_admin_sortByNameDesc() {
        // Exercise
        def channels = channelService.getAccessibleChannelList(admin, [sort:'name', order:'desc'])
        // Verify
        assert channels == [ch3, ch2, ch1]
    }

    void testGetAccessibleChannelList_admin_sortByDescriptionAsc() {
        // Exercise
        def channels = channelService.getAccessibleChannelList(admin, [sort:'description', order:'asc'])
        // Verify
        assert channels == [ch3, ch2, ch1]
    }

    void testGetAccessibleChannelList_user1() {
        // Setup
        def user1 = Person.findByLoginName("user1")
        // Exercise
        def channels = channelService.getAccessibleChannelList(user1, [:])
        // Verify
        assert channels == [ch1]
    }

    void testGetJoinedPersons_test1() {
        // Exercise
        def persons = channelService.getJoinedPersons(ch1)
        // Verify
        assert persons == [user1]
    }

    void testGetJoinedPersons_test3() {
        // Exercise
        def persons = channelService.getJoinedPersons(ch3)
        // Verify
        assert persons == [user3, userX]
    }

    void testGetAllJoinedPersons() {
        // Exercise
        def channels = channelService.getAllJoinedPersons()
        // Verify
        assert channels.size() == 3
        assert channels[ch1] == [user1]
        assert channels[ch2] == [user2]
        assert channels[ch3] == [user3, userX]
    }

    void testRelateToIrclog() {
        // Setup
        createIrclog(permaId:"log:ch1/ch2", channelName:ch1.name, channel:ch2).saveSurely()
        // Verify Fixture
        assert Irclog.findByPermaId("log:ch1:0").channel == null
        assert Irclog.findByPermaId("log:ch1:1").channel == null
        assert Irclog.findByPermaId("log:ch2:0").channel == ch2
        assert Irclog.findByPermaId("log:ch1/ch2").channel == ch2
        // Exercise
        assert channelService.relateToIrclog(ch1) == 2
        // Verify
        assert Irclog.findByPermaId("log:ch1:0").channel == ch1
        assert Irclog.findByPermaId("log:ch1:1").channel == ch1
        assert Irclog.findByPermaId("log:ch2:0").channel == ch2
        assert Irclog.findByPermaId("log:ch1/ch2").channel == ch2
    }

    void testDeleteChannel() {
        // Verify Fixture
        assert Person.findByLoginName("user3").channels.contains(ch3)
        assert Person.findByLoginName("userX").channels.contains(ch3)
        assert Irclog.count() == 6
        assert Irclog.findAllByChannelName("#ch3").every{ it.channel == ch3 }
        assert Summary.countByChannel(ch3) == 1
        assert Channel.get(ch3.id) == ch3
        // Exercise
        channelService.deleteChannel(ch3)
        // Verify
        assert Person.findByLoginName("user3").channels.contains(ch3) == false
        assert Person.findByLoginName("userX").channels.contains(ch3) == false
        assert Irclog.count() == 6
        assert Irclog.findAllByChannelName("#ch3").every{ it.channel == null }
        assert Summary.countByChannel(ch3) == 0
        assert Channel.get(ch3.id) == null
    }

    void testGetBeforeDate_IgnoredOptionType_TRUE() {
        // Setup
        createIrclogAs(ch2, "2010-10-01 12:34:56", "PRIVMSG")
        createIrclogAs(ch2, "2010-10-03 23:59:59", "NOTICE")
        createIrclogAs(ch2, "2010-10-05 12:34:56", "OTHER")
        createIrclogAs(ch2, "2010-10-07 01:23:45", "PRIVMSG")
        // Exercise
        boolean isIgnoredOptionType = true
        Date date = channelService.getBeforeDate("2010-10-06", ch2, isIgnoredOptionType)
        // Verify
        assert date == toDate("2010-10-03 00:00:00")
    }

    void testGetBeforeDate_IgnoredOptionType_FALSE() {
        // Setup
        createIrclogAs(ch2, "2010-10-01 12:34:56", "PRIVMSG")
        createIrclogAs(ch2, "2010-10-03 23:59:59", "NOTICE")
        createIrclogAs(ch2, "2010-10-05 12:34:56", "OTHER")
        createIrclogAs(ch2, "2010-10-07 01:23:45", "PRIVMSG")
        // Exercise
        boolean isIgnoredOptionType = false
        Date date = channelService.getBeforeDate("2010-10-06", ch2, isIgnoredOptionType)
        // Verify
        assert date == toDate("2010-10-05 00:00:00")
    }

    void testGetBeforeDate_Empty() {
        // Exercise
        boolean isIgnoredOptionType = true
        Date date = channelService.getBeforeDate("1999-01-01", ch2, isIgnoredOptionType)
        // Verify
        assert date == null
    }

    void testGetAfterDate_IgnoredOptionType_TRUE() {
        // Setup
        createIrclogAs(ch2, "2010-10-01 12:34:56", "PRIVMSG")
        createIrclogAs(ch2, "2010-10-03 23:59:59", "OTHER")
        createIrclogAs(ch2, "2010-10-05 12:34:56", "NOTICE")
        createIrclogAs(ch2, "2010-10-07 01:23:45", "PRIVMSG")
        // Exercise
        boolean isIgnoredOptionType = true
        Date date = channelService.getAfterDate("2010-10-02", ch2, isIgnoredOptionType)
        // Verify
        assert date == toDate("2010-10-05 00:00:00")
    }

    void testGetAfterDate_IgnoredOptionType_FALSE() {
        // Setup
        createIrclogAs(ch2, "2010-10-01 12:34:56", "PRIVMSG")
        createIrclogAs(ch2, "2010-10-03 23:59:59", "OTHER")
        createIrclogAs(ch2, "2010-10-05 12:34:56", "NOTICE")
        createIrclogAs(ch2, "2010-10-07 01:23:45", "PRIVMSG")
        // Exercise
        boolean isIgnoredOptionType = false
        Date date = channelService.getAfterDate("2010-10-02", ch2, isIgnoredOptionType)
        // Verify
        assert date == toDate("2010-10-03 00:00:00")
    }

    void testGetAfterDate_Empty() {
        // Exercise
        boolean isIgnoredOptionType = true
        Date date = channelService.getAfterDate("2999-01-01", ch2, isIgnoredOptionType)
        // Verify
        assert date == null
    }

    void testGetLatestDate_IgnoredOptionType_TRUE() {
        // Setup
        createIrclogAs(ch2, "2010-10-01 12:34:56", "PRIVMSG")
        createIrclogAs(ch2, "2010-10-03 23:59:59", "OTHER")
        createIrclogAs(ch2, "2010-10-05 12:34:56", "NOTICE")
        createIrclogAs(ch2, "2010-10-07 01:23:45", "OTHER")
        // Exercise
        boolean isIgnoredOptionType = true
        Date date = channelService.getLatestDate("2010-10-02", ch2, isIgnoredOptionType)
        // Verify
        assert date == toDate("2010-10-05 00:00:00")
    }

    void testGetLatestDate_IgnoredOptionType_FALSE() {
        // Setup
        createIrclogAs(ch2, "2010-10-01 12:34:56", "PRIVMSG")
        createIrclogAs(ch2, "2010-10-03 23:59:59", "OTHER")
        createIrclogAs(ch2, "2010-10-05 12:34:56", "NOTICE")
        createIrclogAs(ch2, "2010-10-07 01:23:45", "OTHER")
        // Exercise
        boolean isIgnoredOptionType = false
        Date date = channelService.getLatestDate("2010-10-02", ch2, isIgnoredOptionType)
        // Verify
        assert date == toDate("2010-10-07 00:00:00")
    }

    void testGetLatestDate_Empty() {
        // Exercise
        boolean isIgnoredOptionType = true
        Date date = channelService.getLatestDate("2999-01-01", ch2, isIgnoredOptionType)
        // Verify
        assert date == null
    }

    void testGetRelatedDates() {
        // Setup
        createIrclogAs(ch2, "2010-10-01 12:34:56", "PRIVMSG")
        createIrclogAs(ch2, "2010-10-03 23:59:59", "OTHER")
        createIrclogAs(ch2, "2010-10-05 12:34:56", "NOTICE")
        createIrclogAs(ch2, "2010-10-07 01:23:45", "TOPIC")
        // Exercise
        boolean isIgnoredOptionType = true
        def result = channelService.getRelatedDates("2010-10-04", ch2, isIgnoredOptionType)
        // Verify
        assert result.size() == 3
        assert result.before == toDate("2010-10-01 00:00:00")
        assert result.after  == toDate("2010-10-05 00:00:00")
        assert result.latest == toDate("2010-10-07 00:00:00")
    }

    private createIrclogAs(ch, dateTime, type) {
        Date time = toDate(dateTime)
        createIrclog(permaId:"log:${ch}:${time}", channelName:ch.name, time:time, channel:ch, type:type).saveSurely()
    }
}
