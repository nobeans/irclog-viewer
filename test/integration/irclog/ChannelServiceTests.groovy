package irclog

import static irclog.utils.DomainUtils.*
import static irclog.utils.DateUtils.*
import org.junit.*

class ChannelServiceTests {

    def channelService
    def ch1, ch2, ch3
    def user1, user2, user3, userX, admin

    @Before
    void setUp() {
        setUpChannel()
        setUpPerson()
        setUpRelationBetweenPersonAndChannel()
        setUpIrclog()
        setUpSummary()
    }

    private setUpChannel() {
        (1..3).each { num ->
            this."ch${num}" = createChannel(name:"#ch${num}", description:"${10 - num}").save(failOnError:true)
        }
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
    private setUpIrclog() {
        2.times { id ->
            createIrclog(permaId:"log:ch1:${id}", channelName:ch1.name, time:toDate("2010-01-01"), channel:null).save(failOnError:true)
            createIrclog(permaId:"log:ch2:${id}", channelName:ch2.name, time:toDate("2010-01-01"), channel:ch2).save(failOnError:true)
            createIrclog(permaId:"log:ch3:${id}", channelName:ch3.name, time:toDate("2010-01-01"), channel:ch3).save(failOnError:true)
        }
    }
    private setUpSummary() {
        createSummary(channel:ch1, latestIrclog:Irclog.findByPermaId("log:ch1:0")).save(failOnError:true)
        createSummary(channel:ch2, latestIrclog:Irclog.findByPermaId("log:ch2:0")).save(failOnError:true)
        createSummary(channel:ch3, latestIrclog:Irclog.findByPermaId("log:ch3:0")).save(failOnError:true)
    }

    @Test
    void getAccessibleChannelList_admin() {
        // Exercise
        def channels = channelService.getAccessibleChannelList(admin, [:])
        // Verify
        assert channels == [ch1, ch2, ch3]
    }

    @Test
    void getAccessibleChannelList_admin_sortByNameDesc() {
        // Exercise
        def channels = channelService.getAccessibleChannelList(admin, [sort:'name', order:'desc'])
        // Verify
        assert channels == [ch3, ch2, ch1]
    }

    @Test
    void getAccessibleChannelList_admin_sortByDescriptionAsc() {
        // Exercise
        def channels = channelService.getAccessibleChannelList(admin, [sort:'description', order:'asc'])
        // Verify
        assert channels == [ch3, ch2, ch1]
    }

    @Test
    void getAccessibleChannelList_user1() {
        // Setup
        def user1 = Person.findByLoginName("user1")
        // Exercise
        def channels = channelService.getAccessibleChannelList(user1, [:])
        // Verify
        assert channels == [ch1]
    }

    @Test
    void getJoinedPersons_test1() {
        // Exercise
        def persons = channelService.getJoinedPersons(ch1)
        // Verify
        assert persons == [user1]
    }

    @Test
    void getJoinedPersons_test3() {
        // Exercise
        def persons = channelService.getJoinedPersons(ch3)
        // Verify
        assert persons == [user3, userX]
    }

    @Test
    void getAllJoinedPersons() {
        // Exercise
        def channels = channelService.getAllJoinedPersons()
        // Verify
        assert channels.size() == 3
        assert channels[ch1] == [user1]
        assert channels[ch2] == [user2]
        assert channels[ch3] == [user3, userX]
    }

    @Test
    void relateToIrclog() {
        // Setup
        createIrclog(permaId:"log:ch1/ch2", channelName:ch1.name, channel:ch2).save(failOnError:true)
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

    @Test
    void deleteChannel() {
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

    @Test
    void getBeforeDate_IgnoredOptionType_TRUE() {
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

    @Test
    void getBeforeDate_IgnoredOptionType_FALSE() {
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

    @Test
    void getBeforeDate_Empty() {
        // Exercise
        boolean isIgnoredOptionType = true
        Date date = channelService.getBeforeDate("1999-01-01", ch2, isIgnoredOptionType)
        // Verify
        assert date == null
    }

    @Test
    void getAfterDate_IgnoredOptionType_TRUE() {
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

    @Test
    void getAfterDate_IgnoredOptionType_FALSE() {
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

    @Test
    void getAfterDate_Empty() {
        // Exercise
        boolean isIgnoredOptionType = true
        Date date = channelService.getAfterDate("2999-01-01", ch2, isIgnoredOptionType)
        // Verify
        assert date == null
    }

    @Test
    void getLatestDate_IgnoredOptionType_TRUE() {
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

    @Test
    void getLatestDate_IgnoredOptionType_FALSE() {
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

    @Test
    void getLatestDate_Empty() {
        // Exercise
        boolean isIgnoredOptionType = true
        Date date = channelService.getLatestDate("2999-01-01", ch2, isIgnoredOptionType)
        // Verify
        assert date == null
    }

    @Test
    void getRelatedDates() {
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
        createIrclog(permaId:"log:${ch}:${time}", channelName:ch.name, time:time, channel:ch, type:type).save(failOnError:true)
    }
}
