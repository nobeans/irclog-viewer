package irclog

import grails.test.*
import static irclog.utils.DomainUtils.*

class ChannelServiceTests extends GrailsUnitTestCase {

    def channelService

    protected void setUp() {
        super.setUp()
        (1..3).each {
            def ch = createChannel(name:"#test$it").saveSurely()
            def user = createPerson(loginName:"user$it").saveSurely()
            user.addToRoles(Role.findByName("ROLE_USER"))
            user.addToChannels(ch)
        }
    }

    void testGetAccessibleChannelList_admin() {
        // Setup
        def admin = Person.findByLoginName("admin")
        // Exercise
        def channels = channelService.getAccessibleChannelList(admin, [:])
        // Verify constructures
        assert channels.size() == 3
        assert channels[0].name == "#test1"
        assert channels[1].name == "#test2"
        // Verify type and properties
        def channel = channels[2]
        assert channel.class == Channel
        assert channel.name == "#test3"
        assert channel.description == "#test3 is nice!"
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
        assert channel.name == "#test1"
        assert channel.description == "#test1 is nice!"
        assert channel.isPrivate == true
        assert channel.isArchived == false
        assert channel.secretKey == "1234"
    }

}
