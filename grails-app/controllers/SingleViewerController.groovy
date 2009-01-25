/**
 * IRCログの単独表示モード用コントローラ
 */
class SingleViewerController extends Base {

    def irclogSearchService
    def channelService

    def index = {
        // パラメータを正規化する。
        normalizeParams()

        // 検索条件をパースする。
        def criterion = parseCriterion()

        // ログ一覧を取得する。
        def searchResult = irclogSearchService.search(loginUserDomain, criterion, [:], 'asc')
        flash.message = null
        if (searchResult.totalCount == 0) {
            flash.message = 'singleViewer.search.error.empty'
        }

        // アクセス可能なチャンネルを取得する。
        def selectableChannels = getSelectableChannels()

        // モデルを作成して、デフォルトビューへ。
        def model = [
            irclogList: searchResult.list,
            selectableChannels: selectableChannels,
            criterion: criterion,
            beforeDate: getBeforeDate(selectableChannels),
            afterDate: getAfterDate(selectableChannels),
            nickPersonList: getNickPersonList()
        ]
        render(view:'index', model:model)
    }

    private normalizeParams() {
        log.debug "Original params: " + params
        params.channel = normalizeChannelName(params.channel)
        params.date = normalizeDate(params.date)
        log.debug "Normalized params: " + params
    }
    private normalizeChannelName(channelName) {
        channelName.startsWith('#') ? channelName : '#' + channelName
    }
    private normalizeDate(date) { // YYYYMMDD -> YYYY-MM-DD
        date.replaceAll(/(\d{4})(\d{2})(\d{2})/, "\$1-\$2-\$3")
    }

    /** 現在の日付よりも前で、ログが存在する日付を取得する。 */
    private getBeforeDate(selectableChannels) {
        if (!selectableChannels.keySet().contains(params.channel)) return null
        
        def dates = Irclog.executeNativeQuery("""
            select
                {tbl.*}
            from
                irclog as {tbl}
            where
                time < '${params.date} 00:00:00'
            and
                channel_name = '${params.channel}'
        """ + ((getCurrentTypeInMixed() != 'all') ? """
            and
                type in ('PRIVMSG', 'NOTICE')
        """ : '') + """
            order by
                time desc
            limit 1
        """)
        CollectionUtils.getFirstOrNull(dates)?.time
    }
    /** 現在の日付よりも後で、ログが存在する日付を取得する。*/
    private getAfterDate(selectableChannels) {
        if (!selectableChannels.keySet().contains(params.channel)) return null
        def dates = Irclog.executeNativeQuery("""
            select
                {tbl.*}
            from
                irclog as {tbl}
            where
                time > '${params.date} 23:59:59'
            and
                channel_name = '${params.channel}'
        """ + ((getCurrentTypeInMixed() != 'all') ? """
            and
                type in ('PRIVMSG', 'NOTICE')
        """ : '') + """
            order by
                time asc
            limit 1
        """)
        CollectionUtils.getFirstOrNull(dates)?.time
    }

    private parseCriterion() {
        def criterion = [
            period:      'oneday',
            channel:     normalizeChannelName(params.channel),
            type:        'all',
            currentType: getCurrentTypeInMixed()
        ]
        criterion['period-oneday-date'] = params.date
        criterion.remove('') // 値が空のものを除外
        log.debug "Criterion: " + criterion
        criterion
    }

    private getSelectableChannels() {
        def channels = [:]
        channelService.getAccessibleChannelList(loginUserDomain, params).each { channels[it.name] = it.name }
        channels
    }

    private getNickPersonList() {
        // FIXME:対象chに関連するユーザだけに絞った方がもっと軽くなる
        Person.findAll("from Person as p where p.nicks <> '' and p.color <> ''")
    }

    /** mixed側での現在のtype条件がallかどうか。*/
    private getCurrentTypeInMixed() {
        session['IRCLOG_VIEWER_CRITERION']?.type ?: 'filtered'
    }
}
