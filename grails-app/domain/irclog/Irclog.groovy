package irclog

class Irclog {
    
    static final ESSENTIAL_TYPES = ['PRIVMSG', 'NOTICE', 'TOPIC']
    static final OPTION_TYPES = [
        'JOIN',
        'NICK',
        'QUIT',
        'PART',
        'KICK',
        'MODE',
        'SYSTEM',
        'OTHER',
        'SIMPLE'
    ]
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
        type(inList:ALL_TYPES)
        message()
        nick(blank:false)
        permaId(unique:true)
        channelName(blank:false)
        channel(nullable:true)
    }
    
    static mapping = {
        version(false)
        message(type:'text')
    }
    
    public String toString() {
        "[${new java.text.SimpleDateFormat('yyyy-MM-dd HH:mm:ss').format(time)}] ${type?.toUpperCase()} <${nick}:${channelName}> ${message}"
    }
}