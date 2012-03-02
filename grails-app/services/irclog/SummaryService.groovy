package irclog

import irclog.utils.DateUtils
import java.text.SimpleDateFormat

class SummaryService {

    static transactional = true

    def sqlHelper

    List<Summary> getAccessibleSummaryList(params, accessibleChannelList) {
        // 全てのサマリを取得して、アクセス可能な範囲に絞り込む
        def summaryList = getAllSummaryList(params).findAll{it.channel in accessibleChannelList}

        // 何らかの原因でサマリが存在しないチャンネルがある場合、ダミーサマリを登録
        // ただし、ソート順序は反映されず、一番下に不要なものが並ぶだけとなる。
        accessibleChannelList.findAll{!(it in summaryList.channel)}.sort{ it.name }.each { channel ->
            summaryList << new Summary(channel:channel, lastUpdated:DateUtils.today)
        }

        return summaryList
    }

    private List<Summary> getAllSummaryList(params) {
        // ソート条件を解決してから、サマリを取得
        def originalSort = resolveSortCondition(params)
        def summaryList = Summary.list(params)

        // 独自ソート
        // チャンネル名(channel)
        //  - 関連テーブルの条件になるため、params.sortで指定できないようだ。
        if (params.sort == 'channel') {
            summaryList.sort{it.channel.name}
            return (params.order == 'desc') ? summaryList.reverse() : summaryList
        }

        // 独自ソート
        // 累積件素(total/totalBeforeYesterday)
        //  - 今日の件数を加えた累積件数(total)はDB上に存在しないため、totalBeforeYesterdayに差し替えしている。
        //  - このソート結果自体は不要であるが、他のUI上も有効なソートキーとは異なるキーでなければならない。
        //  - ここでは、使われたソートキーがtotalBeforeYesterdayの場合、total()を使って再ソートする。
        if (originalSort == 'total') {
            params.sort = 'total' // UI上のソートキーに戻す
            summaryList.sort{it.total()}
            return (params.order == 'desc') ? summaryList.reverse() : summaryList
        }

        // 独自ソート
        // 最新のログ(latestIrclog)
        //  - DB上のlatest_irclog_idでソートすると、通常の運用ではIDは発言順に払い出されるため、
        //    ほぼ正しく最新発言日時順にソートされる。
        //  - しかし、バッチインポートによって過去のログをINSERTすると、IDでは期待されたソートはできなくなる。
        //  - とはいえ、その後オンラインインポートによって誰かのログがINSERTされれば、再び期待通りとなる。
        //  - それほど問題とはならないと考えるため、現時点では対処しないこととする。

        return summaryList
    }

    private String resolveSortCondition(params) {
        // orderの指定がないか不正値の場合は、desc固定とする。
        // この表ではチャンネル以外は降順(desc)の方がわかりやすい。
        // 実際はデフォルト時以外は、どちらかがパラメータで指定されているはずである。
        // デフォルトの場合は、後述のswitch文でorderも再設定する。
        if (!params.order || !['asc', 'desc'].contains(params.order)) params.order = 'desc'

        switch (params.sort) {
            case 'total': // UI上では単にtotalであるが、内部的にはいったんtotalBeforeYesterdayにする
                params.sort = 'totalBeforeYesterday' // 何でも良い
                return 'total'
            case Summary.SORTABLE:
                return params.sort // そのまま
            default:
                params.sort = 'latestIrclog'
                params.order = 'desc'
                break
        }
    }

    void updateTodaySummary() {
        def today = DateUtils.today
        def from = today.clearTime()
        def to = (today + 1).clearTime()

        def result = Summary.list().collect { summary ->
            def criteria = Irclog.createCriteria()

            // count
            sumary.today = criteria.get {
                rowCount()
                eq('channel', summary.channel)
                isNotNull('channel') // TODO is needs?
                'in'('type', Irclog.ESSENTIAL_TYPES)
                between('time', from, to)
            }

            // latest message
            summary.latestIrclog = criteria.get {
                eq('channel', summary.channel)
                'in'('type', Irclog.ESSENTIAL_TYPES)
                order('time', 'desc')
            }

            // last updated date
            summary.lastUpdated = today // TODO to use auto timestamping

            summary.save()

            return 1
        }

        log.info "Updated today's summary: " + result
    }

    void updateAllSummary() {

        println "HOGEHGE"


        def resultDeleted = 1
        def resultInserted = 1

        log.info "Updated all summary: deleted(${resultDeleted}), inserted(${resultInserted})"

//        def baseDate = DateUtils.today
//        sqlHelper.withSql { sql ->
//            int resultDeleted = sql.executeUpdate("delete from summary")
//            int resultInserted = sql.executeUpdate("""\
//                |insert into summary (
//                |    id,
//                |    channel_id,
//                |    last_updated,
//                |    today_,
//                |    yesterday,
//                |    two_days_ago,
//                |    three_days_ago,
//                |    four_days_ago,
//                |    five_days_ago,
//                |    six_days_ago,
//                |    total_before_yesterday,
//                |    latest_irclog_id
//                |)
//                |select
//                |    nextval('hibernate_sequence') as id,
//                |    channel_id,
//                |    timestamp ? as last_updated,
//                |    sum(case when date_trunc('day', time) = timestamp ? then 1 else 0 end) as today_,
//                |    sum(case when date_trunc('day', time) = timestamp ? then 1 else 0 end) as yesterday,
//                |    sum(case when date_trunc('day', time) = timestamp ? then 1 else 0 end) as two_days_ago,
//                |    sum(case when date_trunc('day', time) = timestamp ? then 1 else 0 end) as three_days_ago,
//                |    sum(case when date_trunc('day', time) = timestamp ? then 1 else 0 end) as four_days_ago,
//                |    sum(case when date_trunc('day', time) = timestamp ? then 1 else 0 end) as five_days_ago,
//                |    sum(case when date_trunc('day', time) = timestamp ? then 1 else 0 end) as six_days_ago,
//                |    count(*) as total_before_yesterday,
//                |    '' as latest_irclog_id
//                |from
//                |    irclog
//                |where
//                |    channel_id is not null
//                |and
//                |    type in (${expandPlaceholders(Irclog.ESSENTIAL_TYPES)})
//                |group by
//                |    channel_id
//                """.stripMargin(), [
//                    baseDate.format("yyyy-MM-dd HH:mm:ss"),
//                    df.format(baseDate),
//                    df.format(baseDate - 1),
//                    df.format(baseDate - 2),
//                    df.format(baseDate - 3),
//                    df.format(baseDate - 4),
//                    df.format(baseDate - 5),
//                    df.format(baseDate - 6),
//                    *Irclog.ESSENTIAL_TYPES,
//                ])
//        }
    }

    private expandPlaceholders(list) {
        (["?"] * list.size()).join(", ")
    }
}
