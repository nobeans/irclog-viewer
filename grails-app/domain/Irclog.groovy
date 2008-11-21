class Irclog {

    private static final TYPE_LIST = ['all', 'PRIVMSG', 'NOTICE', 'JOIN', 'NICK', 'QUIT', 'PART', 'KICK', 'MODE', 'TOPIC', 'SYSTEM', 'OTHER', 'SIMPLE']

    Date time
    String type
    String message
    String nick
    Boolean isHidden

    String channelName // インポート時にログからそのまま格納する
    Channel channel    // チャンネルが登録済みの場合は関連として保持する

    static belongsTo = Channel

    static constraints = {
        time()
        type(inList:TYPE_LIST)
        message()
        nick(blank:false)
        isHidden()
        channelName(blank:false)
        channel(nullable:true)
    }
 
    public String toString() {
        def str = "[${DateUtils.format(time)}] ${type?.toUpperCase()} <${nick}:${channelName}> ${message} "
        (isHidden) ? "(${str})" : str
    }

    // チャンネルが登録済みの場合は関連づける
    def beforeUpdate = {
        channel = Channel.findByName(channelName)
    }
    def beforeInsert = beforeUpdate
}
