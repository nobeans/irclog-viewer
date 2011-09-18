package irclog

import grails.test.mixin.*
import org.junit.*
import static irclog.utils.DomainUtils.*

@TestFor(Irclog)
class IrclogTests {

    @Before
    void setUp() {
        mockForConstraintsTests(Irclog, [
            createIrclog(permaId:'EXISTS_PERMAID')
        ])
    }

    @Test
    void validate_OK_channel_null() {
        Irclog irclog = createIrclog(channel:null)
        assert irclog.validate()
    }

    @Test
    void validate_OK_channel_notNull() {
        Irclog irclog = createIrclog(channel:createChannel())
        assert irclog.validate()
    }

    @Test
    void validate_NG_type_undefined() {
        Irclog irclog = createIrclog(type:'UNDEFINED_TYPE')
        assert irclog.validate() == false
        assert irclog.errors['type'].code == 'not.inList'
    }

    @Test
    void validate_NG_nick_blank() {
        Irclog irclog = createIrclog(nick:'')
        assert irclog.validate() == false
        assert irclog.errors['nick'].code == 'blank'
    }

    @Test
    void validate_NG_permaId_notUnique() {
        Irclog irclog = createIrclog(permaId:'EXISTS_PERMAID')
        assert irclog.validate() == false
        assert irclog.errors['permaId'].code == 'unique'
    }

    @Test
    void validate_NG_channelName_blank() {
        Irclog irclog = createIrclog(channelName:'')
        assert irclog.validate() == false
        assert irclog.errors['channelName'].code == 'blank'
    }
}
