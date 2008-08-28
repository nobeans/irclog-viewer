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

        // ページングのために、max/offsetをparamsにコピーしておく。
        params.max = criterion.max
        params.offset = criterion.offset

        // モデルを作成して、デフォルトビューへ。
        [
            irclogList: getIrclogList(criterion),
            irclogCount: countIrclogList(criterion),
            selectableChannels: getSelectableChannels(),
            selectableScopes: getSelectableScopes(),
            criterion: criterion
        ]
    }
    
    private parseCriterion(params) {
        def criterion = [
            scope:     params.scope,
            channelId: params.channelId,
            nicks:     params.nick?.split(/\s+/) as List,    // スペース区切りで複数OR指定可能
            messages:  params.message?.split(/\s+/) as List, // スペース区切りで複数OR指定可能
            nick:      params.nick,                          // そのままのもとっておく
            message:   params.message,                       // そのままのもとっておく
            max:       params.max ? Long.valueOf(params.max) : 50,
            offset:    params.offset ? Long.valueOf(params.offset) : 0
        ]
        //criterion.findAll{ !(it.value instanceof String && it.value == '') }
        criterion
    }

    private getIrclogList(criterion) {
        def query = createQuery(criterion)

        // ソート条件(固定)
        // 時系列ですべての許可されたログをソートする。チャンネル別にしないところがポイント。
        if (query.hql) query.hql += " order by i.time"

        Irclog.findAll(query.hql, query.args, [max:criterion.max, offset:criterion.offset])
    }
    private countIrclogList(criterion) {
        def query = createQuery(criterion)
        Long.valueOf(Irclog.executeQuery("select count(i) " + query.hql, query.args)[0])
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
            query.args << Long.valueOf(criterion.channelId)
        } else if (criterion.channelId == 'all') { // 許可されたチャンネルすべて
            def channels = getAccessableChannels()
            if (channels) {
                query.hql += " and ( " + channels.collect{"i.channel.id like ?"}.join(" or ") + " )"
                query.args.addAll(channels.collect{Long.valueOf(it.id)})
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
        query.args << "resolveBeginDate_${criterion.scope}"()

        // ニックネーム
        if (criterion.nicks) {
            query.hql += " and ( " + criterion.nicks.collect{"i.nick like ?"}.join(" or ") + " )"
            query.args.addAll(criterion.nicks.collect{"%${it}%"})
        }

        // メッセージ
        if (criterion.messages) {
            query.hql += " and ( " + criterion.messages.collect{"i.message like ?"}.join(" or ") + " )"
            query.args.addAll(criterion.messages.collect{"%${it}%"})
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

    private resolveBeginDate_hour() {
        def cal = Calendar.getInstance()
        cal.add(Calendar.HOUR_OF_DAY, -1)
        cal.getTime()
    }
    private resolveBeginDate_today() {
        getCalendarAtZeroHourOfToday().getTime()
    }
    private resolveBeginDate_week() {
        def cal = getCalendarAtZeroHourOfToday()
        cal.add(Calendar.DATE, -7)
        cal.getTime()
    }
    private resolveBeginDate_month() {
        def cal = getCalendarAtZeroHourOfToday()
        cal.add(Calendar.MONTH, -1)
        cal.getTime()
    }
    private resolveBeginDate_year() {
        def cal = getCalendarAtZeroHourOfToday()
        cal.add(Calendar.YEAR, -1)
        cal.getTime()
    }
    private resolveBeginDate_all() {
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
        hour:'1時間以内', today:'今日のみ', week:'1週間以内', month:'1ヶ月以内', year:'1年以内', all:'すべて'
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
