package irclog

import irclog.utils.DateUtils

class SummaryUpdateService {

    static transactional = true

    def sqlHelper

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
