class IrclogSearchService {

    def channelService

    def search(criterion, params) {
        def query = createQuery(criterion)
        [
            list: findAll(query, params),
            totalCount: count(query),
            message: query.message
        ]
    }

    private findAll(query, params) {
        // ソート条件(固定)
        // 時系列ですべての許可されたログをソートする。チャンネル別にしないところがポイント。
        Irclog.findAll(query.hql + " order by i.time", query.args, params)
    }

    private count(query) {
        Irclog.executeQuery("select count(i) " + query.hql, query.args)[0].toInteger()
    }

    private createQuery(criterion) {
        def query = [
            hql: "from Irclog as i where 1 = 1",
            args: [],
            message: []
        ]

        // 対象期間
        if (!criterion.period) criterion.period = 'hour' // デフォルトは1時間以内
        //query.hql += " and i.time between ? and ?"
        query.hql += " and i.time >= ? and i.time < ?"
        query.args << "resolveBeginDate_${criterion.period}"(criterion)
        query.args << "resolveEndDate_${criterion.period}"(criterion)

        // チャンネル
        if (criterion.channelId && criterion.channelId.isLong()) { // 指定された1つのチャンネル
            // 許可されていない場合は、何事もなかったかのように、とぼける。
            if (!channelService.getAccessableChannels().any{ it.id.toString() == criterion.channelId }) {
                query.message = "チャンネルを指定してください。"
                criterion.channelId = null
                query.hql += " and 1 = 0" // 許可されたチャンネルが0件であれば、絶対にヒットさせない
                return query // channelId未指定の場合は、ヒット件数0件とする(デフォルト挙動)
            }
            query.hql += " and i.channel.id = ?"
            query.args << criterion.channelId.toLong()
        } else if (criterion.channelId == 'all') { // 許可されたチャンネルすべて
            def channels = channelService.getAccessableChannels()
            if (channels) {
                query.hql += " and ( " + channels.collect{"i.channel.id like ?"}.join(" or ") + " )"
                query.args.addAll(channels.collect{it.id.toLong()})
            } else {
                query.hql += " and 1 = 0" // 許可されたチャンネルが0件であれば、絶対にヒットさせない
            }
        } else {
            query.message = "チャンネルを指定してください。"
            query.hql += " and 1 = 0" // 許可されたチャンネルが0件であれば、絶対にヒットさせない
            return query // channelId未指定の場合は、ヒット件数0件とする(デフォルト挙動)
        }

        // 種別
        if (criterion.type != 'all') { // すべての種別でなければ限定条件を追加する。
          query.hql += " and i.type = ?"
          query.args << criterion.type
        }

        // ニックネーム
        def nicks = criterion.nick?.split(/\s+/) as List // スペース区切りで複数OR指定可能
        if (nicks) {
            query.hql += " and ( " + nicks.collect{"i.nick like ?"}.join(" or ") + " )"
            query.args.addAll(nicks.collect{"%${it}%"})
        }

        // メッセージ
        def messages = criterion.message?.split(/\s+/) as List // スペース区切りで複数OR指定可能
        if (messages) {
            query.hql += " and ( " + messages.collect{"i.message like ?"}.join(" or ") + " )"
            query.args.addAll(messages.collect{"%${it}%"})
        }

        // GString→String変換
        [
            hql: query.hql.toString(),
            args: query.args.collect{ (it instanceof GString) ? it.toString() : it }
        ]
    }

    // ----------------------------------------------
    // 対象期間:〜から
    private resolveBeginDate_hour(criterion) {
        def cal = Calendar.getInstance()
        cal.add(Calendar.HOUR_OF_DAY, -1)
        cal.getTime()
    }
    private resolveBeginDate_today(criterion) {
        getCalendarAtZeroHourOfToday().getTime()
    }
    private resolveBeginDate_oneday(criterion) {
        try {
            return new java.text.SimpleDateFormat('yyyy-MM-dd').parse(criterion['period-oneday-date'] ?: '')
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
    private resolveBeginDate_year(criterion) {
        def cal = getCalendarAtZeroHourOfToday()
        cal.add(Calendar.YEAR, -1)
        cal.getTime()
    }
    private resolveBeginDate_all(criterion) {
        new Date(0) // エポックタイム
    }

    // ----------------------------------------------
    // 対象期間:〜まで
    private resolveEndDate_hour(criterion) {
        getCalendarAtZeroHourOfToday().getTime()
    }
    private resolveEndDate_today(criterion) {
        getCalendarAtZeroHourOfToday().getTime()
    }
    private resolveEndDate_oneday(criterion) {
        try {
            def onedayDate = new java.text.SimpleDateFormat('yyyy-MM-dd').parse(criterion['period-oneday-date'] ?: '')
            def cal = Calendar.getInstance()
            cal.setTime(onedayDate)
            cal.add(Calendar.DATE, 1)
            return cal.getTime()
        } catch (java.text.ParseException e) {
            return new Date(0) // エポックタイム==絶対にヒットさせない
        }
    }
    private resolveEndDate_week(criterion) {
        getCalendarAtZeroHourOfToday().getTime()
    }
    private resolveEndDate_month(criterion) {
        getCalendarAtZeroHourOfToday().getTime()
    }
    private resolveEndDate_year(criterion) {
        getCalendarAtZeroHourOfToday().getTime()
    }
    private resolveEndDate_all(criterion) {
        getCalendarAtZeroHourOfToday().getTime()
    }

    private getCalendarAtZeroHourOfToday(cal = Calendar.getInstance()) {
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal
    }

}
