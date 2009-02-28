import java.text.SimpleDateFormat

class SummaryService {

    /** 基本種別をIN句で使うための文字列 */
    private static final String IN_ESSENTIAL_TYPES = "(" + Irclog.ESSENTIAL_TYPES.collect{"'${it}'"}.join(', ') + ")"

    boolean transactional = false

    /** 全チャンネルのサマリ情報を取得する。 */
    public List<Summary> getAllSummaryList() {
        updateTodaySummary()
        Summary.list()
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
                latest_irclog_id = tbl.latest_irclog_id 
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

        // 今日の分のサマリで更新対象がない＝全体サマリが生成されていない
        if (result == 0) updateAllSummary()
    }

    /** 全ての分のサマリを更新する。 */
    public void updateAllSummary() {
        def baseDate = new Date()
        def df = new SimpleDateFormat("yyyy-MM-dd")
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
}
