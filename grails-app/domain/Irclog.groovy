class Irclog {

    private static final TYPE_LIST = ['all', 'PRIVMSG', 'NOTICE', 'JOIN', 'NICK', 'QUIT', 'PART', 'KICK', 'MODE', 'TOPIC', 'SYSTEM', 'OTHER', 'SIMPLE']

    Date time
    String type
    String message
    String nick
    Boolean isHidden

    Channel channel

    static belongsTo = Channel

    static constraints = {
        time()
        type(inList:TYPE_LIST)
        message(blank:false)
        nick()
        isHidden()
        channel()
    }
 
}
