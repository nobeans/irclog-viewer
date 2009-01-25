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
     * すべてのチャンネルに対する関連付け済みのユーザを取得する。
     * @return キー=Channel, 値=[Person...] のMap
     */
    def getAllJoinedPersons() {
        def result = [:]
        Channel.list().each { ch ->
            result[ch] = Person.executeQuery("select p from Person as p join p.channels as c where c = ?", ch)
        }
        result
    }

    /** 
     * 現在のチャンネル定義を元に、まだチャンネルに関連付けできていないIrclogの関連更新を試みる。
     */
    def relateToIrclog(channel) {
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


    /** 
     * 指定のチャンネルを削除する。
     * 各種の関連付けを適切に削除する。
     */
    def deleteChannel(channel) {
        // チャンネルとユーザの関連付けを削除する。
        // 関連付けレコード自体を削除する。
        Irclog.executeUpdateNativeQuery("delete from person_channel where channel_id = :channelId", null, [channelId:channel.id])

        // チャンネルとログの関連付けを削除する。
        // nullで更新するだけで、ログレコードの削除はしない。
        Irclog.executeUpdateNativeQuery("update irclog set channel_id = null where channel_id = :channelId", null, [channelId:channel.id])

        // チャンネルを削除する。
        channel.delete()
    }

}
