class IrclogSearchService {

    boolean transactional = false

    def channelService

    def search(person, criterion, params) {
        def query = createQuery(person, criterion)
        [
            list: findAll(query, params),
            totalCount: count(query),
            message: query.message
        ]
    }

    private findAll(query, params) {
        // ソート条件(固定)
        // 時系列ですべての許可されたログをソートする。チャンネル別にしないところがポイント。
        Irclog.findAll(query.hql + " order by i.time desc", query.args, params)
    }

    private count(query) {
        Irclog.executeQuery("select count(i) " + query.hql, query.args)[0].toInteger()
    }

    private createQuery(person, criterion) {
        def query = [
            hql: "from Irclog as i where 1 = 1",
            args: [],
            message: []
        ]

        // 対象期間
        assert (criterion.period) : '必須'
        if (!criterion.period) criterion.period = 'hour' // デフォルトは1時間以内
        query.hql += " and i.time >= ? and i.time < ?"
        query.args << "resolveBeginDate_${criterion.period}"(criterion)
        query.args << "resolveEndDate_${criterion.period}"(criterion)

        // チャンネル
        assert (criterion.channel) : '必須'
        def channels = channelService.getAccessibleChannelList(person, criterion)
        if (criterion.channel.isLong()) { // 指定された1つのチャンネル
            // 許可されていない場合は、何事もなかったかのように、とぼける。
            if (!channels.any{ it.id.toString() == criterion.channel }) {
                query.message = 'viewer.search.error.notFoundChannel'
                criterion.channel = null
                query.hql += " and 1 = 0" // 許可されたチャンネルが0件であれば、絶対にヒットさせない
                return query // channel未指定の場合は、ヒット件数0件とする(デフォルト挙動)
            }
            query.hql += " and i.channel.id = ?"
            query.args << criterion.channel.toLong()
        } else if (criterion.channel == 'all') { // 許可されたチャンネルすべて
            if (channels) {
                query.hql += " and ( " + channels.collect{"i.channel.id = ?"}.join(" or ") + " )"
                query.args.addAll(channels.collect{it.id.toLong()})
            } else {
                query.hql += " and 1 = 0" // 許可されたチャンネルが0件であれば、絶対にヒットさせない
            }
        }

        // 種別
        assert (criterion.type) : '必須'
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
    private resolveEndDate_today(criterion) {
        getCalendarAtZeroHourOfTomorrow().getTime()
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
        getCalendarAtZeroHourOfTomorrow().getTime()
    }
    private resolveEndDate_month(criterion) {
        getCalendarAtZeroHourOfTomorrow().getTime()
    }
    private resolveEndDate_year(criterion) {
        getCalendarAtZeroHourOfTomorrow().getTime()
    }
    private resolveEndDate_all(criterion) {
        getCalendarAtZeroHourOfTomorrow().getTime()
    }

    private getCalendarAtZeroHourOfToday(cal = Calendar.getInstance()) {
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal
    }
    private getCalendarAtZeroHourOfTomorrow() {
        def cal = getCalendarAtZeroHourOfToday()
        cal.add(Calendar.DATE, 1) // 翌日
        cal
    }

}
