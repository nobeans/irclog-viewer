package irclog

import grails.test.mixin.*
import org.junit.*
import static irclog.utils.DomainUtils.*

@TestFor(Channel)
class ChannelTests {

    @Before
    void setUp() {
        // it's necessary to describe "channel.errors['xxxx']".
        mockForConstraintsTests(Channel)
    }

    @Test
    void validate_OK_publicWithoutSecretKey() {
        Channel channel = createChannel(
            isPrivate: true,
            secretKey: '1234',
        )
        assert channel.validate()
    }

    @Test
    void validate_OK_privateWithSecretKey() {
        Channel channel = createChannel(
            isPrivate: false,
            secretKey: '',
        )
        assert channel.validate()
    }

    @Test
    void validate_NG_name_notStartingWithHash() {
        Channel channel = createChannel(
            name:"test",
        )
        assert channel.validate() == false
        assert channel.errors['name'] == 'matches'
    }

    @Test
    void validate_NG_privateWithoutSecretKey() {
        Channel channel = createChannel(
            isPrivate: true,
            secretKey: '',
        )
        assert channel.validate() == false
        assert channel.errors['secretKey'] == 'validator'
    }

    @Test
    void validate_NG_publicWithSecretKey() {
        Channel channel = createChannel(
            isPrivate: false,
            secretKey: 'SHOULD_BE_EMPTY',
        )
        assert channel.validate() == false
        assert channel.errors['secretKey'] == 'validator'
    }

    @Test
    void equals() {
        assert createChannel(name:"#test1") == createChannel(name:"#test1")
        assert createChannel(name:"#test2") == createChannel(name:"#test2")
        assert createChannel(name:"#test3") == createChannel(name:"#test3")
        assert createChannel(name:"#test1") != createChannel(name:"#test2")
    }
}
