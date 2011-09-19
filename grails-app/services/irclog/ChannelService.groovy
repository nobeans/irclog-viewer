package irclog

import groovy.sql.Sql
import static irclog.utils.DateUtils.*

class ChannelService {

    def transactional = true

    def sqlHelper

    /** アクセス可能な全チャンネルを取得する。 */
    List<Channel> getAccessibleChannelList(person, params) {
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
    List<Person> getJoinedPersons(Channel ch) {
        Person.executeQuery("select p from Person as p join p.channels as c where c = ?", [ch])
    }

    /**
     * すべてのチャンネルに対する関連付け済みのユーザを取得する。
     * @return キー=Channel, 値=[Person...] のMap
     */
    Map<Channel, List<Person>> getAllJoinedPersons() {
        def result = [:]
        Channel.list().each { ch ->
            result[ch] = getJoinedPersons(ch)
        }
        result
    }

    /**
     * 現在のチャンネル定義を元に、まだチャンネルに関連付けできていないIrclogの関連更新を試みる。
     */
    int relateToIrclog(channel) {
        return sqlHelper.executeUpdate("""
            update
                irclog as i
            set
                channel_id = ${channel.id}
            where
                i.channel_id is null
            and
                i.channel_name = ${channel.name}
        """)
    }

    /**
     * 指定のチャンネルを削除する。
     * 各種の関連付けを適切に削除する。
     */
    void deleteChannel(channel) {
        sqlHelper.withSql { sql ->
            // チャンネルとユーザの関連付けを削除する。
            // 関連付けレコード自体を削除する。
            sql.executeUpdate("delete from person_channel where channel_id = ${channel.id}")

            // チャンネルとログの関連付けを削除する。
            // nullで更新するだけで、ログレコードの削除はしない。
            sql.executeUpdate("update irclog set channel_id = null where channel_id = ${channel.id}")

            // サマリが存在している場合は削除する。
            sql.executeUpdate("delete from summary where channel_id = ${channel.id}")
        }

        // チャンネルを削除する。
        channel.delete()
    }

    /**
     * 以下の日付三種を取得する。
     * それぞれ、該当する日付が存在しない場合はnullを返す。
     * <ul>
     * <li>before: 現在の日付よりも前で、ログが存在する日付</li>
     * <li>after: 現在の日付よりも後で、ログが存在する最新日付</li>
     * <li>latest: 現在の日付よりも後で、ログが存在する日付</li>
     * </ul>
     * @param date "yyyy-MM-dd"
     */
    Map<String, Date> getRelatedDates(date, channel, isIgnoredOptionType) {
        [
            before: getBeforeDate(date, channel, isIgnoredOptionType),
            after:  getAfterDate (date, channel, isIgnoredOptionType),
            latest: getLatestDate(date, channel, isIgnoredOptionType),
        ]
    }
    /** 現在の日付よりも前で、ログが存在する日付を取得する。 */
    private Date getBeforeDate(dateStr, channel, isIgnoredOptionType) {
        def c = Irclog.createCriteria()
        def list = c.list {
            and {
                lt "time", toDate(dateStr + " 00:00:00")
                eq "channel", channel
                if (isIgnoredOptionType) {
                    'in' "type", Irclog.ESSENTIAL_TYPES
                }
            }
            order("time", "desc")
            maxResults(1)
        }.collect { resetTimeToOrigin(it.time) }
        return list.getAt(0)
    }
    /** 現在の日付よりも後で、ログが存在する日付を取得する。*/
    private Date getAfterDate(dateStr, channel, isIgnoredOptionType) {
        def c = Irclog.createCriteria()
        def list = c.list {
            and {
                gt "time", toDate(dateStr + " 23:59:59")
                eq("channel", channel)
                if (isIgnoredOptionType) {
                    'in' "type", Irclog.ESSENTIAL_TYPES
                }
            }
            order("time", "asc")
            maxResults(1)
        }.collect { resetTimeToOrigin(it.time) }
        return list.getAt(0)
    }
    /** 現在の日付よりも後で、ログが存在する最新日付を取得する。*/
    private Date getLatestDate(dateStr, channel, isIgnoredOptionType) {
        def c = Irclog.createCriteria()
        def list = c.list {
            and {
                gt "time", toDate(dateStr + " 23:59:59")
                eq "channel", channel
                if (isIgnoredOptionType) {
                    'in' "type", Irclog.ESSENTIAL_TYPES
                }
            }
            order("time", "desc")
            maxResults(1)
        }.collect { resetTimeToOrigin(it.time) }
        return list.getAt(0)
    }
}
