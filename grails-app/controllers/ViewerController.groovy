/**
 * IRCログの表示処理のコントローラ。
 */
class ViewerController {
    
    /**
     * ログ一覧を表示する。
     */
    def index = {
        // 検索条件をパースする。
        def criterion = parseCriterion(params)

        // ページングのために、max/offsetをセットアップする。
        params.max = params.max?.toInteger() ?: 50
        params.offset = params.offset?.toInteger() ?: 0

        // モデルを作成して、デフォルトビューへ。
        [
            irclogList: getIrclogList(criterion, [max:params.max, offset:params.offset]),
            irclogCount: countIrclogList(criterion),
            selectableChannels: getSelectableChannels(),
            selectableScopes: getSelectableScopes(),
            criterion: criterion
        ]
    }

    // paginateタグのparams属性でcriterionを渡すと、リクエストのparamsスコープのmax/offset値が
    // params属性で渡しkたcriterion中のmax/offsetで上書きされてしまい、戻るボタンのページ遷移がおかしくなる。
    // よって、max/offsetはcriterionとして取り扱わない。
    private parseCriterion(params) {
        def criterion = [
            scope:              params.scope,
            channelId:          params.channelId,
            nick:               params.nick,
            message:            params.message
        ]
        if (criterion.scope == 'specified') {
            criterion['scope-specified'] = params['scope-specified']
        }
        criterion
    }

    private getIrclogList(criterion, params) {
        def query = createQuery(criterion)

        // ソート条件(固定)
        // 時系列ですべての許可されたログをソートする。チャンネル別にしないところがポイント。
        if (query.hql) query.hql += " order by i.time"

        Irclog.findAll(query.hql, query.args, params)
    }
    private countIrclogList(criterion) {
        def query = createQuery(criterion)
        Irclog.executeQuery("select count(i) " + query.hql, query.args)[0].toInteger()
    }

    private createQuery(criterion) {
        def query = [
            hql: "from Irclog as i where 1 = 1",
            args: []
        ]

        // チャンネル
        if (criterion.channelId && criterion.channelId.isLong()) { // 指定された1つのチャンネル
            // 許可されていない場合は、何事もなかったかのように、とぼける。
            if (!getAccessableChannels().any{ it.id.toString() == criterion.channelId }) {
                flash.message = "チャンネルを指定してください。"
                criterion.channelId = null
                query.hql += " and 1 = 0" // 許可されたチャンネルが0件であれば、絶対にヒットさせない
                return query // channelId未指定の場合は、ヒット件数0件とする(デフォルト挙動)
            }
            query.hql += " and i.channel.id = ?"
            query.args << criterion.channelId.toInteger()
        } else if (criterion.channelId == 'all') { // 許可されたチャンネルすべて
            def channels = getAccessableChannels()
            if (channels) {
                query.hql += " and ( " + channels.collect{"i.channel.id like ?"}.join(" or ") + " )"
                query.args.addAll(channels.collect{it.id.toLong()})
            } else {
                query.hql += " and 1 = 0" // 許可されたチャンネルが0件であれば、絶対にヒットさせない
            }
        } else {
            flash.message = "チャンネルを指定してください。"
            query.hql += " and 1 = 0" // 許可されたチャンネルが0件であれば、絶対にヒットさせない
            return query // channelId未指定の場合は、ヒット件数0件とする(デフォルト挙動)
        }

        // 対象期間
        if (!criterion.scope) criterion.scope = 'hour' // デフォルトは1時間以内
        query.hql += " and i.time >= ?"
        query.args << "resolveBeginDate_${criterion.scope}"(criterion)

        // ニックネーム
        def nicks = params.nick?.split(/\s+/) as List // スペース区切りで複数OR指定可能
        if (nicks) {
            query.hql += " and ( " + nicks.collect{"i.nick like ?"}.join(" or ") + " )"
            query.args.addAll(nicks.collect{"%${it}%"})
        }

        // メッセージ
        def messages = params.message?.split(/\s+/) as List // スペース区切りで複数OR指定可能
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

    private getCalendarAtZeroHourOfToday() {
        def cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal
    }

    private resolveBeginDate_hour(criterion) {
        def cal = Calendar.getInstance()
        cal.add(Calendar.HOUR_OF_DAY, -1)
        cal.getTime()
    }
    private resolveBeginDate_today(criterion) {
        getCalendarAtZeroHourOfToday().getTime()
    }
    private resolveBeginDate_specified(criterion) {
        try {
            return new java.text.SimpleDateFormat('yyyy-MM-dd').parse(criterion['scope-specified'])
        } catch (java.text.ParseException e) {
            flash.message = '指定日の日付形式が不正です。'
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

    private getSelectableChannels() {
        def channels = [:]
        channels[''] = '未指定'
        getAccessableChannels().each { channels[it.id] = '#' + it.name }
        channels['all'] = 'すべて'
        channels
    }

    private getAccessableChannels() {
        Channel.findAll('from Channel as c where c.isPublic = true order by c.name')
    }

    private getSelectableScopes() {[
        hour:'1時間以内', today:'今日のみ', specified:'指定日...', week:'1週間以内', month:'1ヶ月以内', year:'1年以内', all:'すべて'
    ]}

    /** ログの対象行の非表示・表示をトグルする。*/
    def hideOrShow = {
        def irclog = Irclog.get(params.id)
        if (irclog) {
            irclog.isHidden = !irclog.isHidden  // トグル
            if (!irclog.hasErrors() && irclog.save()) {
                flash.message = "ログレコードを表示にしました。"
                flash.args = [params.id]
                flash.defaultMessage = "Irclog ${params.id} updated"
            }
        }
        redirect(action:show, id:irclog.id)
    }

}
