import java.text.SimpleDateFormat

class SummaryService {

    /** 基本種別をIN句で使うための文字列 */
    private static final String IN_ESSENTIAL_TYPES = "(" + Irclog.ESSENTIAL_TYPES.collect{"'${it}'"}.join(', ') + ")"

    boolean transactional = true

    def irclogSearchService

    /** アクセス可能な全チャンネルのトピック情報(1週間以内の上位5件)を取得する。*/
    public List<Irclog> getAccessibleTopicList(person, accessibleChannelList) {
        def baseDate = new Date()
        def df = new SimpleDateFormat("yyyy-MM-dd")
        def baseDateFormatted = df.format(baseDate)
        Irclog.executeNativeQuery("""
            select
                *
            from
                irclog
            where
                channel_id in ( ${accessibleChannelList.collect{it.id}.join(", ")} )
            and
                type = 'TOPIC'
            and
                time >= timestamp '${baseDateFormatted}' - interval '6 day'
            order by
                time desc
            limit 5
        """)
    }

    /** アクセス可能な全チャンネルのサマリ情報を取得する。 */
    public List<Summary> getAccessibleSummaryList(params, accessibleChannelList) {
        // 全てのサマリを取得して、アクセス可能な範囲に絞り込む
        def summaryList = getAllSummaryList(params).findAll{it.channel in accessibleChannelList}

        // FIXME: 単に表示しない、というだけで良い気がする。後で削除するかも。
        // 何らかの原因でサマリが存在しないチャンネルがある場合、ダミーサマリを登録
        // ただし、ソート順序は反映されず、一番下に不要なものが並ぶだけとなる。
        accessibleChannelList.findAll{!(it in summaryList.channel)}.sort{it.name}.each { channel ->
            summaryList << new Summary(channel:channel, lastUpdated:new Date())
        }

        return summaryList
    }

    /** 全チャンネルのサマリ情報を取得する。 */
    private List<Summary> getAllSummaryList(params) {
        // サマリ更新
        updateSummary()

        // ソート条件を解決してから、サマリを取得
        def originalSort = resolveSortCondition(params)
        def summaryList = Summary.list(params)
        if (params.timeMarker) {
            setupTodayAfterTimeMarker(summaryList, params.timeMarker)
        }

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

        // 独自ソート
        // 累積件素(total/totalBeforeYesterday)
        //  - 今日の件数を加えた累積件数(total)はDB上に存在しないため、totalBeforeYesterdayに差し替えしている。
        //  - このソート結果自体は不要であるが、他のUI上も有効なソートキーとは異なるキーでなければならない。
        //  - ここでは、使われたソートキーがtotalBeforeYesterdayの場合、total()を使って再ソートする。
        if (originalSort == 'todayAfterTimeMarker') {
            params.sort = originalSort // UI上のソートキーに戻す
            summaryList.sort{it.todayAfterTimeMarker}
            println(summaryList)
            return (params.order == 'desc') ? summaryList.reverse() : summaryList
        }

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
            case 'todayAfterTimeMarker':
                params.sort = 'totalBeforeYesterday' // 何でも良い
                return 'todayAfterTimeMarker'
            case Summary.SORTABLE:
                return params.sort // そのまま
            default:
                params.sort = 'latestIrclog'
                params.order = 'desc'
                break
        }
    }

    /**
     * サマリを更新する。
     * 以下のいずれかの場合は、全てのサマリを更新する。
     * - (A) サマリが0件の場合
     * - (B) lastUpdatedが今日以外のサマリレコードが1件以上存在する場合
     * - (C) サマリレコード件数がirclogテーブル上にログが存在するチャンネル総数と一致しない場合
     *       →これで救えるのは追加されたばかりのチャンネルに対する過去ログレコードのサマリ情報であるが、
     *         チャンネル追加時に全サマリ更新処理を実行するため、対応は不要。(A)(B)のみとする。
     * それ以外の場合は、今日のサマリのみを更新する。
     */
    private void updateSummary() {
        def baseDate = new Date()
        def df = new SimpleDateFormat("yyyy-MM-dd")
        def baseDateFormatted = df.format(baseDate)

        // (A) サマリが0件の場合
        if (Summary.count() == 0) {
            log.info "サマリが0件のため、全サマリを更新します。"
            updateAllSummary()
            return
        }

        // (B) lastUpdatedが今日以外のサマリレコードが1件以上存在するかどうか
        int summaryCountInPast = Summary.executeQuery("select count(*) from Summary as s where s.lastUpdated < '${baseDateFormatted} %'")[0]
        if (summaryCountInPast > 0) {
            log.info "lastUpdatedが今日以外のサマリレコードが1件以上存在するため、全サマリを更新します。"
            updateAllSummary()
            return
        }

        updateTodaySummary()
    }

    /** 今日の分のサマリを更新する。 */
    private void updateTodaySummary() {
        def baseDate = new Date()
        def df = new SimpleDateFormat("yyyy-MM-dd")
        def baseDateFormatted = df.format(baseDate)
        int result = Summary.executeUpdateNativeQuery("""
            update
                summary
            set
                today = tbl.today,
                latest_irclog_id = tbl.latest_irclog_id,
                last_updated = timestamp '${new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(baseDate)}'
            from
                (
                    select
                        channel_id,
                        count(*) as today,
                        (select id from irclog as i where i.channel_id = irclog.channel_id and i.time = max(irclog.time) order by time desc limit 1) as latest_irclog_id
                    from
                        irclog
                    where
                        channel_id is not null
                    and
                        date_trunc('day', time) = timestamp '${baseDateFormatted}'
                    and
                        type in ${IN_ESSENTIAL_TYPES}
                    group by
                        channel_id
                ) as tbl
            where
                summary.channel_id = tbl.channel_id
            and
                date_trunc('day', summary.last_updated) = timestamp '${baseDateFormatted}'
        """)
        log.info "Updated today's summary: " + result
    }

    /** 全ての分のサマリを更新する。 */
    public void updateAllSummary() {
        def baseDate = new Date()
        def df = new SimpleDateFormat("yyyy-MM-dd")

        // タイミングによっては重複したINSERTが実行されることもありうるため、排他的テーブルロックを取得する。
        Summary.executeUpdateNativeQuery("lock table summary in exclusive mode")
        int resultDeleted = Summary.executeUpdateNativeQuery("delete from summary")
        int resultInserted = Summary.executeUpdateNativeQuery("""
            insert into summary
                (id, channel_id, last_updated, today, yesterday, two_days_ago, three_days_ago, four_days_ago, five_days_ago, six_days_ago, total_before_yesterday, latest_irclog_id) 
            select
                nextval('hibernate_sequence') as id,
                channel_id,
                timestamp '${new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(baseDate)}' as last_updated,
                sum(case when date_trunc('day', time) = timestamp '${df.format(baseDate)}'     then 1 else 0 end) as today,
                sum(case when date_trunc('day', time) = timestamp '${df.format(baseDate - 1)}' then 1 else 0 end) as yesterday,
                sum(case when date_trunc('day', time) = timestamp '${df.format(baseDate - 2)}' then 1 else 0 end) as two_days_ago,
                sum(case when date_trunc('day', time) = timestamp '${df.format(baseDate - 3)}' then 1 else 0 end) as three_days_ago,
                sum(case when date_trunc('day', time) = timestamp '${df.format(baseDate - 4)}' then 1 else 0 end) as four_days_ago,
                sum(case when date_trunc('day', time) = timestamp '${df.format(baseDate - 5)}' then 1 else 0 end) as five_days_ago,
                sum(case when date_trunc('day', time) = timestamp '${df.format(baseDate - 6)}' then 1 else 0 end) as six_days_ago,
                count(*) as total_before_yesterday,
                (select id from irclog as i where i.channel_id = irclog.channel_id and i.time = max(irclog.time) order by time desc limit 1) as latest_irclog_id
            from
                irclog
            where
                channel_id is not null
            and
                type in ${IN_ESSENTIAL_TYPES}
            group by
                channel_id
        """)
        log.info "Updated all summary: deleted(${resultDeleted}), inserted(${resultInserted})"
    }

    private void setupTodayAfterTimeMarker(List<Summary> summaryList, Date timeMarker) {
        // 0:Integer(count), 1:Channel
        def result = Irclog.withCriteria {
            projections {
                rowCount()
            }
            isNotNull('channel')
            ge('time', timeMarker)
            'in'('type', Irclog.ESSENTIAL_TYPES)
            groupProperty('channel')
        }
        // タイムマーカ以降の件数を反映する。
        summaryList.each { summary ->
            summary.todayAfterTimeMarker = result.find{ it[1] == summary.channel }?.getAt(0) ?: 0
        }
    }

}
