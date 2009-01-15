class Irclog {

    private static final TYPE_LIST = ['all', 'PRIVMSG', 'NOTICE', 'JOIN', 'NICK', 'QUIT', 'PART', 'KICK', 'MODE', 'TOPIC', 'SYSTEM', 'OTHER', 'SIMPLE']

    Date time
    String type
    String message
    String nick
    String permaId // パーマID (パーマリンクに使用する)

    String channelName // インポート時にログからそのまま格納する
    Channel channel    // チャンネルが登録済みの場合は関連として保持する

    static belongsTo = Channel

    static constraints = {
        time()
        type(inList:TYPE_LIST)
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
        def str = "[${new java.text.SimpleDateFormat('yyyy-MM-dd HH:mm:ss').format(time)}] ${type?.toUpperCase()} <${nick}:${channelName}> ${message}"
    }
}
