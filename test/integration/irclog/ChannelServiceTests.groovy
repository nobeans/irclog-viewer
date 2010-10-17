package irclog

import grails.test.*
import static irclog.utils.DomainUtils.*

class ChannelServiceTests extends GrailsUnitTestCase {

    def channelService
    def ch1, ch2, ch3
    def user1, user2, user3, userX, admin

    protected void setUp() {
        super.setUp()

        // Person & Channel
        // #ch1 [user1]
        // #ch2 [user2]
        // #ch3 [user3, userX]
        def roleUser = Role.findByName("ROLE_USER")
        (1..3).each { num ->
            def ch = createChannel(name:"#ch${num}", description:"${10 - num}").saveSurely()
            def user = createPerson(loginName:"user${num}").saveSurely()
            user.addToRoles(roleUser)
            user.addToChannels(ch)
            this."ch${num}" = ch
            this."user${num}" = user
        }
        userX = createPerson(loginName:"userX").saveSurely()
        userX.addToRoles(roleUser)
        userX.addToChannels(ch3)
        admin = Person.findByLoginName("admin") // setup in Bootstrap

        // Irclog
        2.times { id ->
            createIrclog(permaId:"log:ch1:${id}", channelName:ch1.name, channel:null).saveSurely()
            createIrclog(permaId:"log:ch2:${id}", channelName:ch2.name, channel:ch2).saveSurely()
            createIrclog(permaId:"log:ch3:${id}", channelName:ch3.name, channel:ch3).saveSurely()
        }

        // Summary
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
}
