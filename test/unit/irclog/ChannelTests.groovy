package irclog

import com.sun.net.httpserver.Filter.Chain;
import grails.test.mixin.*
import org.junit.*

@TestFor(Channel)
class ChannelTests {

    @Before
    void setUp() {
        mockDomain(Channel)
        setUpDatabase()
    }

    void testValidate_OK() {
        Channel channel = createChannel("#test")
        assert channel.validate()
    }

    void testValidate_NG_name_notStartingWithHash() {
        Channel channel = createChannel("test")
        assert channel.validate() == false
    }

    void testFindAll() {
        def actual = Channel.findAll()
        assert actual.size() == 2
    }

    private Channel createChannel(name) {
        def mockChannel = new Channel(name:name)
        mockChannel.description = "説明文です"
        mockChannel.isPrivate = true
        mockChannel.isArchived = true
        mockChannel.secretKey = "1234"
        mockChannel
    }

    private setUpDatabase() {
        assert createChannel("#prepared1").save()
        assert createChannel("#prepared2").save()
    }
}
