package irclog

import irclog.utils.DateUtils

class IrclogSearchService {

    /** 基本種別をIN句で使うための文字列 */
    private static final String IN_ESSENTIAL_TYPES = "(" + Irclog.ESSENTIAL_TYPES.collect { "'${it}'" }.join(', ') + ")"

    static transactional = false

    def channelService

    def search(person, criterion, params, direction = 'desc') {
        def query = createQuery(person, criterion)
        [
            list: findAllIrclogs(query, params, direction),
            totalCount: count(query)
        ]
    }

    private findAllIrclogs(query, params, direction) {
        // ソート条件(固定)
        // 時系列ですべての許可されたログをソートする。チャンネル別にしないところがポイント。
        Irclog.findAll(query.hql + " order by i.time ${direction}, i.id ${direction}", query.args, params)
    }

    private count(query) {
        Irclog.executeQuery("select count(i) " + query.hql, query.args)[0].toInteger()
    }

    private createQuery(person, criterion) {
        def query = [
            hql: "from Irclog as i where 1 = 1",
            args: []
        ]

        // 対象期間
        assert (criterion.period): 'required'
        query.hql += " and i.time >= ? and i.time < ?"
        query.args << "resolveBeginDate_${criterion.period}"(criterion)
        query.args << "resolveEndDate_${criterion.period}"(criterion)

        // チャンネル
        assert (criterion.channel): 'required'
        def accesibleChannels = channelService.getAccessibleChannelList(person, criterion)
        if (criterion.channel == 'all') { // 許可されたチャンネルすべて
            if (accesibleChannels) {
                query.hql += " and ( " + accesibleChannels.collect { "i.channel.id = ?" }.join(" or ") + " )"
                query.args.addAll(accesibleChannels.collect { it.id })
            } else {
                query.hql += " and 1 = 0" // 許可されたチャンネルが0件であれば、絶対にヒットさせない
            }
        } else { // 指定された1つのチャンネル
            def channel = accesibleChannels.find { it.name == criterion.channel }
            if (!channel) {
                // 許可されていない場合は、何事もなかったかのように、とぼける。
                query.hql += " and 1 = 0" // 許可されたチャンネルが0件であれば、絶対にヒットさせない
                return query // channel未指定の場合は、ヒット件数0件とする(デフォルト挙動)
            }
            query.hql += " and i.channel.id = ?"
            query.args << channel.id
        }

        // 種別
        assert (criterion.type): 'required'
        if (criterion.type != 'all') { // すべての種別であれば条件なし
            if (criterion.type == 'filtered') { // 限定条件を追加する。
                query.hql += " and i.type in " + IN_ESSENTIAL_TYPES
            } else {
                query.hql += " and i.type = '${criterion.type}'"
            }
        }

        // ニックネーム
        if (criterion.nick) {
            def nicks = criterion.nick?.split(/\s+/) as List // スペース区切りで複数OR指定可能
            query.hql += " and ( " + nicks.collect { "lower(i.nick) like lower(?)" }.join(" or ") + " )"
            query.args.addAll(nicks.collect { "%${it}%" })
        }

        // メッセージ
        if (criterion.message) {
            def messages = criterion.message?.split(/\s+/) as List // スペース区切りで複数OR指定可能
            query.hql += " and ( " + messages.collect { "lower(i.message) like lower(?)" }.join(" or ") + " )"
            query.args.addAll(messages.collect { "%${it}%" })
        }

        // GString→String変換
        [
            hql: query.hql.toString(),
            args: query.args.collect {
                (it instanceof GString) ? it.toString() : it
            }
        ]
    }

    // ----------------------------------------------
    // 対象期間:〜から
    private resolveBeginDate_today(criterion) {
        getCalendarAtZeroHourOfToday().getTime()
    }

    private resolveBeginDate_oneday(criterion) {
        try {
            return new java.text.SimpleDateFormat('yyyy-MM-dd').parse(criterion['periodOnedayDate'] ?: '')
        } catch (java.text.ParseException e) {
            def cal = getCalendarAtZeroHourOfToday()
            cal.add(Calendar.DATE, 1) // 絶対にヒットさせない
            return cal.getTime()
        }
    }

    private resolveBeginDate_week(criterion) {
        def cal = getCalendarAtZeroHourOfToday()
        cal.add(Calendar.DATE, -7)
        cal.getTime()
    }

    private resolveBeginDate_month(criterion) {
        def cal = getCalendarAtZeroHourOfToday()
        cal.add(Calendar.MONTH, -1)
        cal.getTime()
    }

    private resolveBeginDate_halfyear(criterion) {
        def cal = getCalendarAtZeroHourOfToday()
        cal.add(Calendar.MONTH, -6)
        cal.getTime()
    }

    private resolveBeginDate_year(criterion) {
        def cal = getCalendarAtZeroHourOfToday()
        cal.add(Calendar.YEAR, -1)
        cal.getTime()
    }

    private resolveBeginDate_all(criterion) {
        DateUtils.epoch
    }

    // ----------------------------------------------
    // 対象期間:〜まで
    private resolveEndDate_today(criterion) {
        getCalendarAtZeroHourOfTomorrow().getTime()
    }

    private resolveEndDate_oneday(criterion) {
        try {
            def onedayDate = new java.text.SimpleDateFormat('yyyy-MM-dd').parse(criterion['periodOnedayDate'] ?: '')
            def cal = DateUtils.today.toCalendar()
            cal.setTime(onedayDate)
            cal.add(Calendar.DATE, 1)
            return cal.getTime()
        } catch (java.text.ParseException e) {
            return DateUtils.epoch // force never to match
        }
    }

    private resolveEndDate_week(criterion) {
        getCalendarAtZeroHourOfTomorrow().getTime()
    }

    private resolveEndDate_month(criterion) {
        getCalendarAtZeroHourOfTomorrow().getTime()
    }

    private resolveEndDate_halfyear(criterion) {
        getCalendarAtZeroHourOfTomorrow().getTime()
    }

    private resolveEndDate_year(criterion) {
        getCalendarAtZeroHourOfTomorrow().getTime()
    }

    private resolveEndDate_all(criterion) {
        getCalendarAtZeroHourOfTomorrow().getTime()
    }

    private getCalendarAtZeroHourOfToday() {
        DateUtils.today.toCalendar().clearTime()
    }

    private getCalendarAtZeroHourOfTomorrow() {
        def cal = getCalendarAtZeroHourOfToday()
        cal.add(Calendar.DATE, 1) // 翌日
        cal
    }
}
