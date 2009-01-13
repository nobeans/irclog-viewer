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
    def updateChannelOfIrclog(channel) {
        def query = """
            update
                irclog as i
            set
                channel_id = (select id from channel where name = :channelName)
            where
                i.channel_id is null
            and
                i.channel_name = :channelName
        """
        Irclog.executeUpdateNativeQuery(query, null, [channelName:channel.name]) // native-sql plugin (modified)
    }

}
