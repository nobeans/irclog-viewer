class Irclog {

    Date time
    String type
    String message
    String nick
    Boolean isHidden

    Channel channel
    static belongsTo = Channel

    static constraints  = {
        time     nullable:false
        type     nullable:false, inList:typeList
        message  nullable:false, blank:false
        nick     nullable:false, blank:true
        isHidden nullable:false
        channel  nullable:false
    }

    static final typeList = ['all', 'PRIVMSG', 'NOTICE', 'JOIN', 'NICK', 'QUIT', 'PART', 'KICK', 'MODE', 'TOPIC', 'SYSTEM', 'OTHER', 'SIMPLE']
 
}
