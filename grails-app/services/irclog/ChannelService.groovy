package irclog

import static irclog.utils.DateUtils.*

class ChannelService {

    def transactional = true

    def sqlHelper

    /** アクセス可能な全チャンネルを取得する。 */
    List<Channel> getAccessibleChannelList(Person person, Map params) {
        if (!params.sort || !Channel.constraints.keySet().contains(params.sort)) params.sort = 'name'
        if (!params.order || !['asc', 'desc'].contains(params.order)) params.order = 'asc'
        if (person) {
            // 管理者ロールの場合は、全チャンネルにアクセス可能
            if (person.admin) {
                return Channel.list(params)
            }
            // 利用者ロールの場合は、公開チャンネル＋関連付け有りの非公開チャンネル
            return Channel.findAllByIsPrivateOrIdInList(false, person.channels*.id as List, params)
        }
        // 未ログインユーザの場合は、公開チャンネルのみ
        return Channel.findAllByIsPrivate(false, params)
    }

    /**
     * 現在のチャンネル定義を元に、まだチャンネルに関連付けできていないIrclogの関連更新を試みる。
     */
    int relateToIrclog(channel) {
        return sqlHelper.executeUpdate("""\
            |update
            |    irclog as i
            |set
            |    channel_id = ?
            |where
            |    i.channel_id is null
            |and
            |    i.channel_name = ?
            |""".stripMargin(), [channel.id, channel.name])
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
    Map<String, Date> getRelatedDates(Object date, Object channel) {
        [
            before: getBeforeDate(date, channel),
            after: getAfterDate(date, channel),
            latest: getLatestDate(date, channel),
        ]
    }
    /** 現在の日付よりも前で、ログが存在する日付を取得する。 */
    private Date getBeforeDate(Object dateStr, Object channel) {
        def c = Irclog.createCriteria()
        def list = c.list {
            and {
                lt "time", toDate(dateStr + " 00:00:00")
                eq "channel", channel
                'in' "type", Irclog.ESSENTIAL_TYPES
            }
            order("time", "desc")
            maxResults(1)
        }.collect { copyDateWithoutTime(it.time) }
        return list.getAt(0)
    }
    /** 現在の日付よりも後で、ログが存在する日付を取得する。*/
    private Date getAfterDate(Object dateStr, Object channel) {
        def c = Irclog.createCriteria()
        def list = c.list {
            and {
                gt "time", toDate(dateStr + " 23:59:59")
                eq("channel", channel)
                'in' "type", Irclog.ESSENTIAL_TYPES
            }
            order("time", "asc")
            maxResults(1)
        }.collect { copyDateWithoutTime(it.time) }
        return list.getAt(0)
    }
    /** 現在の日付よりも後で、ログが存在する最新日付を取得する。*/
    private Date getLatestDate(Object dateStr, Object channel) {
        def c = Irclog.createCriteria()
        def list = c.list {
            and {
                gt "time", toDate(dateStr + " 23:59:59")
                eq "channel", channel
                'in' "type", Irclog.ESSENTIAL_TYPES
            }
            order("time", "desc")
            maxResults(1)
        }.collect { copyDateWithoutTime(it.time) }
        return list.getAt(0)
    }

    private static Date copyDateWithoutTime(date) {
        new Date(date.time).clearTime()
    }
}
