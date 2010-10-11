package irclog

import groovy.sql.Sql
import irclog.utils.CollectionUtils

class ChannelService {

    /** 基本種別をIN句で使うための文字列 */
    private static final String IN_ESSENTIAL_TYPES = "(" + Irclog.ESSENTIAL_TYPES.collect{"'${it}'"}.join(', ') + ")"

    static transactional = true

    def dataSource

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
            result[ch] = getJoinedPersons(ch)
        }
        result
    }

    /**
     * 現在のチャンネル定義を元に、まだチャンネルに関連付けできていないIrclogの関連更新を試みる。
     */
    public int relateToIrclog(channel) {
        return Irclog.findAllByChannelIsNullAndChannelName(channel.name).each { it.channel = channel }.size()
//        def db = new Sql(dataSource.connection)
//        try {
//            return db.executeUpdate("""
//                update
//                    irclog as i
//                set
//                    channel_id = ${channel.id}
//                where
//                    i.channel_id is null
//                and
//                    i.channel_name = ${channel.name}
//            """)
//        } finally {
//            db.close()
//        }
    }

    /**
     * 指定のチャンネルを削除する。
     * 各種の関連付けを適切に削除する。
     * TODO カスケードを有効活用できないか？
     */
    public void deleteChannel(channel) {
        def db = new Sql(dataSource)
        try {
            // チャンネルとユーザの関連付けを削除する。
            // 関連付けレコード自体を削除する。
            db.executeUpdate("delete from person_channel where channel_id = ${channel.id}")

            // チャンネルとログの関連付けを削除する。
            // nullで更新するだけで、ログレコードの削除はしない。
            db.executeUpdate("update irclog set channel_id = null where channel_id = ${channel.id}")
        } finally {
            db.close()
        }

        // サマリが存在している場合は削除する。
        Summary.findByChannel(channel)?.delete()

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
        def db = new Sql(dataSource)
        try {
            def dates = db.firstRow("""
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
            """.toString())
            println ">y"*50
            println dates
            println dates.class
            println "<y"*50
            CollectionUtils.getFirstOrNull(dates)?.time
        } finally {
            db.close()
        }
    }
    /** 現在の日付よりも後で、ログが存在する日付を取得する。*/
    private Date getAfterDate(date, channel, isIgnoredOptionType) {
        def db = new Sql(dataSource)
        try {
            def dates = db.firstRow("""
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
            """.toString())
            println ">z"*50
            println dates
            println dates.class
            println "<z"*50
            CollectionUtils.getFirstOrNull(dates)?.time
        } finally {
            db.close()
        } 
    }
    /** 現在の日付よりも後で、ログが存在する最新日付を取得する。*/
    private Date getLatestDate(date, channel, isIgnoredOptionType) {
        def db = new Sql(dataSource)
        try {
            def dates = db.firstRow("""
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
            """.toString())
            println ">v"*50
            println dates
            println dates.class
            println "<v"*50
            CollectionUtils.getFirstOrNull(dates)?.time
        } finally {
            db.close()
        }
    }
}
