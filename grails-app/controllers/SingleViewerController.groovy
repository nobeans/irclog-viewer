/**
 * IRCログの単独表示モード用コントローラ
 */
class SingleViewerController extends Base {

    def irclogSearchService
    def channelService

    def index = {
        // パラメータを正規化する。
        normalizeParams(params)

        // 検索条件をパースする。
        def criterion = parseCriterion(params)

        // モデルを作成して、デフォルトビューへ。
        def searchResult = irclogSearchService.search(loginUserDomain, criterion, [:], 'asc')
        flash.message = null
        if (searchResult.totalCount == 0) {
            flash.message = 'singleViewer.search.error.empty'
        }
        def model = [
            irclogList: searchResult.list,
            selectableChannels: getSelectableChannels(),
            criterion: criterion,
            beforeDate: getBeforeDate(params),
            afterDate: getAfterDate(params)
        ]
        render(view:'index', model:model)
    }

    private normalizeParams(params) {
        params.channel = normalizeChannelName(params.channel)
        params.date = normalizeDate(params.date)
    }
    private normalizeChannelName(channelName) {
        channelName.startsWith('#') ? channelName : '#' + channelName
    }
    private normalizeDate(date) { // YYYYMMDD -> YYYY-MM-DD
        date.replaceAll(/(\d{4})(\d{2})(\d{2})/, "\$1-\$2-\$3")
    }

    private getBeforeDate(params) {
        def dates = Irclog.executeNativeQuery("""
            select
                {tbl.*} from irclog as {tbl}
            where
                time < '${params.date} 00:00:00'
            and
                channel_name = '${params.channel}'
            order by
                time desc
            limit 1
        """)
        CollectionUtils.getFirstOrNull(dates)?.time
    }
    private getAfterDate(params) {
        def dates = Irclog.executeNativeQuery("""
            select
                {tbl.*} from irclog as {tbl}
            where
                time > '${params.date} 23:59:59'
            and
                channel_name = '${params.channel}'
            order by
                time asc
            limit 1
        """)
        CollectionUtils.getFirstOrNull(dates)?.time
    }

    private parseCriterion(params) {
        def criterion = [
            period:    'oneday',
            channel:   normalizeChannelName(params.channel),
            type:      params.type ?: 'all'
        ]
        criterion['period-oneday-date'] = params.date
        criterion.findAll{ it.value }
    }

    private getSelectableChannels() {
        def channels = [:]
        channelService.getAccessibleChannelList(loginUserDomain, params).each { channels[it.name] = it.name }
        channels
    }
}
