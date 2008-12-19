class ChannelService {

    boolean transactional = true

    /** アクセス可能な全チャンネルを取得する。 */
    def getAccessibleChannelList(person, params) {
        if (!params.sort || !Channel.constraints.keySet().contains(params.sort)) params.sort = 'name'
        if (!params.order || !['asc', 'desc'].contains(params.order)) params.order = 'asc'
        if (person) {
            return Person.executeQuery("""
                select
                    ch
                from
                    Person as p
                right outer join
                    p.channels as ch
                where
                    p.id = ?
                or
                    (ch.isPrivate = false and p is null)
                order by
                    ch.${params.sort} ${params.order}
            """, person.id)
        } else {
            return Channel.executeQuery("""
                select
                    ch
                from
                    Channel as ch
                where
                    ch.isPrivate = false
                order by
                    ch.${params.sort} ${params.order}
            """)
        }
    }

    /** 
     * 現在のチャンネル定義を元に、まだチャンネルに関連付けできていないIrclogの関連更新を試みる。
     */
    def updateAllChannelOfIrclog() {
        // FIXME: 生SQLのUPDATE文であれば一発で良いのだが・・・。
        def channels = Channel.list()
        def irclogs = Irclog.findAll("from Irclog as i where i.channel is null")
        irclogs.each { irclog -> irclog.channel = channels.find{it.name == irclog.channelName} }
    }

    /** 
     * 現在のチャンネル定義を元に、まだチャンネルに関連付けできていないIrclogの関連更新を試みる。
     */
    def updateChannelOfIrclog(channel) {
        def irclogs = Irclog.findAll("from Irclog as i where i.channel is null and i.channelName = ?", [channel.name])
        irclogs.each { irclog -> irclog.channel = channel }
    }

}
