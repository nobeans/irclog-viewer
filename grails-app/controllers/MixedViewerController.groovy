/**
 * IRCログのミックス表示モード用コントローラ。
 */
class MixedViewerController extends Base {

    def irclogSearchService
    def channelService

    final SELECTABLE_PERIODS = ['all', 'year', 'month', 'week', 'today', 'oneday']
    
    /**
     * ログ一覧を表示する。
     */
    def index = {
        // パラメータを正規化する。
        normalizeParams(params)

        // 検索条件をパースする。
        def criterion = parseCriterion(params)

        // モデルを作成して、デフォルトビューへ。
        def searchResult = irclogSearchService.search(loginUserDomain, criterion, [max:params.max, offset:params.offset])
        if (searchResult.message) flash.message = searchResult.message
        def model = [
            irclogList: searchResult.list,
            irclogCount: searchResult.totalCount,
            selectableChannels: getSelectableChannels(),
            selectablePeriods: SELECTABLE_PERIODS,
            criterion: criterion
        ]
        render(view:'index', model:model)
    }

    private normalizeParams(params) {
        // ページングのために、max/offsetをセットアップする。
        params.max = params.max?.toInteger() ? Math.min(params.max?.toInteger(), config.irclog.viewer.defaultMax) : config.irclog.viewer.defaultMax
        params.offset = params.offset?.toInteger() ?: 0
    }

    // MEMO:
    // paginateタグのparams属性でcriterionを渡すと、リクエストのparamsスコープのmax/offset値が
    // params属性で渡したcriterion中のmax/offsetで上書きされてしまい、戻るボタンのページ遷移がおかしくなる。
    // よって、max/offsetはcriterionとして取り扱わない。
    private parseCriterion(params) {
        def criterion = [
            period:    params.period ?: 'today',
            channel:   params.channel ?: 'all',
            type:      params.type ?: 'all',
            nick:      params.nick,
            message:   params.message
        ]
        if (criterion.period == 'oneday') {
            criterion['period-oneday-date'] = params['period-oneday-date']
        }
        criterion.findAll{ it.value }
    }

    private getSelectableChannels() {
        def channels = [:]
        channels['all'] = message(code:'mixedViewer.search.channel.all')
        channelService.getAccessibleChannelList(loginUserDomain, params).each { channels[it.name] = it.name }
        channels
    }

}
