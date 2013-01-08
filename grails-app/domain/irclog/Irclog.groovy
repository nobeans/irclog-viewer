package irclog

import java.security.MessageDigest

class Irclog {

    static final ESSENTIAL_TYPES = ['PRIVMSG', 'NOTICE', 'TOPIC']
    static final OPTION_TYPES = ['JOIN', 'NICK', 'QUIT', 'PART', 'KICK', 'MODE', 'SYSTEM', 'OTHER', 'SIMPLE', 'INVITE', 'QUICK']
    static final ALL_TYPES = ESSENTIAL_TYPES + OPTION_TYPES

    Date time
    String type
    String message
    String nick
    String permaId // permanent ID for perma-link
    String channelName // just store channel name imported from log
    Channel channel    // make relation if channel has registered yet

    static belongsTo = Channel

    static constraints = {
        time()
        type inList: ALL_TYPES
        message()
        nick blank: false
        permaId unique: true
        channelName blank: false
        channel nullable: true
    }

    static mapping = {
        version false
        message type: 'text'
    }

    @Override
    String toString() {
        "[${time.format('yyyy-MM-dd HH:mm:ss:SSS')}] ${type} #${permaId} ${channelName}(${channel?.id}) <${nick}> ${message}"
    }

    def beforeValidate() {
        updatePermaId()
    }

    private updatePermaId() {
        def base = "${time},${channelName},${nick},${type},${message}"
        this.permaId = MessageDigest.getInstance("MD5").digest(base.getBytes("UTF-8")).collect { String.format("%02x", it & 0xff) }.join()
        assert permaId.size() == 32
    }
}
