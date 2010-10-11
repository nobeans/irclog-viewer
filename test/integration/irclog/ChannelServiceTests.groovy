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

        // aditional user for #ch3
        userX = createPerson(loginName:"userX").saveSurely()
        userX.addToRoles(roleUser)
        userX.addToChannels(ch3)

        admin = Person.findByLoginName("admin") // setup in Bootstrap
    }

    void testGetAccessibleChannelList_admin() {
        // Exercise
        def channels = channelService.getAccessibleChannelList(admin, [:])
        // Verify constructures
        assert channels.size() == 3
        assert channels[0].name == "#ch1"
        assert channels[1].name == "#ch2"
        // Verify type and properties
        def channel = channels[2]
        assert channel.class == Channel
        assert channel.name == "#ch3"
        assert channel.description == "#ch3 is nice!"
        assert channel.isPrivate == true
        assert channel.isArchived == false
        assert channel.secretKey == "1234"
    }

    void testGetAccessibleChannelList_user1() {
        // Setup
        def user1 = Person.findByLoginName("user1")
        // Exercise
        def channels = channelService.getAccessibleChannelList(user1, [:])
        // Verify constructures
        assert channels.size() == 1
        // Verify type and properties
        def channel = channels[0]
        assert channel.class == Channel
        assert channel.name == "#ch1"
        assert channel.description == "#ch1 is nice!"
        assert channel.isPrivate == true
        assert channel.isArchived == false
        assert channel.secretKey == "1234"
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
}
