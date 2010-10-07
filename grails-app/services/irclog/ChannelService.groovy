package irclog
class ChannelService {

    /** 基本種別をIN句で使うための文字列 */
    private static final String IN_ESSENTIAL_TYPES = "(" + Irclog.ESSENTIAL_TYPES.collect{"'${it}'"}.join(', ') + ")"

    boolean transactional = true

    /** アクセス可能な全チャンネルを取得する。 */
    public List<Channel> getAccessibleChannelList(person, params) {
        if (!params.sort || !Channel.constraints.keySet().contains(params.sort)) params.sort = 'name'
        if (!params.order || !['asc', 'desc'].contains(params.order)) params.order = 'asc'
        if (person) {
            // 管理者ロールの場合は、全チャンネルにアクセス可能
            if (person.isAdmin()) {
                return Channel.list(params)
            }
            // 利用者ロールの場合は、公開チャンネル＋関連付け有りの非公開チャンネル
            return Person.executeQuery("""
                select distinct
                    ch
                from
                    Person as p
                right outer join
                    p.channels as ch
                where
                    p.id = ?
                or
                    ch.isPrivate = false
                order by
                    ch.${params.sort} ${params.order}
            """, [person.id])
        }
        // 未ログインユーザの場合は、公開チャンネルのみ
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

    /**
     * 指定されたチャンネルに対する関連付け済みのユーザを取得する。
     * @return キー=Channel, 値=[Person...] のMap
     */
    public List<Person> getJoinedPersons(Channel ch) {
        Person.executeQuery("select p from Person as p join p.channels as c where c = ?", [ch])
    }

    /**
     * すべてのチャンネルに対する関連付け済みのユーザを取得する。
     * @return キー=Channel, 値=[Person...] のMap
     */
    public Map<Channel, List<Person>> getAllJoinedPersons() {
        def result = [:]
        Channel.list().each { ch ->
            result[ch] = Person.executeQuery("select p from Person as p join p.channels as c where c = ?", [ch])
        }
        result
    }

    /**
     * 現在のチャンネル定義を元に、まだチャンネルに関連付けできていないIrclogの関連更新を試みる。
     */
    public int relateToIrclog(channel) {
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
    public void deleteChannel(channel) {
        // チャンネルとユーザの関連付けを削除する。
        // 関連付けレコード自体を削除する。
        Irclog.executeUpdateNativeQuery("delete from person_channel where channel_id = :channelId", null, [channelId:channel.id])

        // チャンネルとログの関連付けを削除する。
        // nullで更新するだけで、ログレコードの削除はしない。
        Irclog.executeUpdateNativeQuery("update irclog set channel_id = null where channel_id = :channelId", null, [channelId:channel.id])

        // サマリが存在している場合は削除する。
        println Summary.findByChannel(channel)?.delete()

        // チャンネルを削除する。
        channel.delete()
    }

    /**
     * 以下の日付三種を取得する。
     * <ul>
     * <li>現在の日付よりも前で、ログが存在する日付</li>
     * <li>現在の日付よりも後で、ログが存在する最新日付</li>
     * <li>現在の日付よりも後で、ログが存在する日付</li>
     * </ul>
     */
    public Map<String, Date> getRelatedDates(selectableChannels, date, channel, isIgnoredOptionType) {
        if (!selectableChannels.keySet().contains(channel.name)) return [:]
        [
            before: getBeforeDate(date, channel, isIgnoredOptionType),
            after:  getAfterDate(date, channel, isIgnoredOptionType),
            latest: getLatestDate(date, channel, isIgnoredOptionType)
        ]
    }
    /** 現在の日付よりも前で、ログが存在する日付を取得する。 */
    private Date getBeforeDate(date, channel, isIgnoredOptionType) {
        def dates = Irclog.executeNativeQuery("""
            select
                {tbl.*}
            from
                irclog as {tbl}
            where
                time < '${date} 00:00:00'
            and
                channel_id = '${channel.id}'
        """ + (isIgnoredOptionType ? """ and type in ${IN_ESSENTIAL_TYPES} """ : '') + """
            order by
                time desc
            limit 1
        """)
        CollectionUtils.getFirstOrNull(dates)?.time
    }
    /** 現在の日付よりも後で、ログが存在する日付を取得する。*/
    private Date getAfterDate(date, channel, isIgnoredOptionType) {
        def dates = Irclog.executeNativeQuery("""
            select
                {tbl.*}
            from
                irclog as {tbl}
            where
                time > '${date} 23:59:59'
            and
                channel_id = '${channel.id}'
        """ + (isIgnoredOptionType ? """ and type in ${IN_ESSENTIAL_TYPES} """ : '') + """
            order by
                time asc
            limit 1
        """)
        CollectionUtils.getFirstOrNull(dates)?.time
    }
    /** 現在の日付よりも後で、ログが存在する最新日付を取得する。*/
    private Date getLatestDate(date, channel, isIgnoredOptionType) {
        def dates = Irclog.executeNativeQuery("""
            select
                {tbl.*}
            from
                irclog as {tbl}
            where
                time > '${date} 23:59:59'
            and
                channel_id = '${channel.id}'
        """ + (isIgnoredOptionType ? """ and type in ${IN_ESSENTIAL_TYPES} """ : '') + """
            order by
                time desc
            limit 1
        """)
        CollectionUtils.getFirstOrNull(dates)?.time
    }
}
