package irclog

import grails.test.*
import static irclog.utils.DomainUtils.*

class ChannelServiceTests extends GrailsUnitTestCase {

    def channelService
    def ch1, ch2, ch3
    def user1, user2, user3, userX, admin

    protected void setUp() {
        super.setUp()

        def roleUser = Role.findByName("ROLE_USER")

        // #ch1 [user1]
        // #ch2 [user2]
        // #ch3 [user3, userX]
        (1..3).each {
            def ch = createChannel(name:"#ch$it").saveSurely()
            def user = createPerson(loginName:"user$it").saveSurely()
            user.addToRoles(roleUser)
            user.addToChannels(ch)
            this."ch${it}" = ch
            this."user${it}" = user
        }
        userX = createPerson(loginName:"userX").saveSurely()
        userX.addToRoles(roleUser)
        userX.addToChannels(ch3)

        admin = Person.findByLoginName("admin") // setup in Bootstrap
    }

    void testGetAccessibleChannelList_admin() {
        // Exercise
        def channels = channelService.getAccessibleChannelList(admin, [:])
        // Verify
        assert channels == [ch1, ch2, ch3]
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
        def log1 = createIrclog(channelName:ch1.name, channel:null).saveSurely()
        def log2 = createIrclog(channelName:ch1.name, channel:null).saveSurely()
        def log3 = createIrclog(channelName:ch1.name, channel:ch2).saveSurely()
        def log4 = createIrclog(channelName:ch2.name, channel:ch2).saveSurely()
        // Exercise
        assert channelService.relateToIrclog(ch1) == 2
        // Verify
        assert Irclog.get(log1.id).channel == ch1
        assert Irclog.get(log2.id).channel == ch1
        assert Irclog.get(log3.id).channel == ch2
        assert Irclog.get(log4.id).channel == ch2
    }

}
