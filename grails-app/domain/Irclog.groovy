class Irclog {

    public static final MANDATORY_TYPES = ['PRIVMSG', 'NOTICE', 'TOPIC']
    public static final OPTION_TYPES = ['JOIN', 'NICK', 'QUIT', 'PART', 'KICK', 'MODE', 'SYSTEM', 'OTHER', 'SIMPLE']
    public static final ALL_TYPES = MANDATORY_TYPES + OPTION_TYPES

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
        type(inList:['all'] + ALL_TYPES) // UI上の選択肢として「すべて」を先頭に追加
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
