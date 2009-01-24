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
        normalizeParams()

        // 検索条件をパースする。
        def criterion = parseCriterion()

        // ログ一覧を取得する。
        def searchResult = irclogSearchService.search(loginUserDomain, criterion, [max:params.max, offset:params.offset])
        flash.message = null

        // モデルを作成して、デフォルトビューへ。
        def model = [
            irclogList: searchResult.list,
            irclogCount: searchResult.totalCount,
            selectableChannels: getSelectableChannels(),
            selectablePeriods: SELECTABLE_PERIODS,
            criterion: criterion,
            nickPersonList: getNickPersonList()
        ]
        render(view:'index', model:model)
    }

    private normalizeParams() {
        log.debug "Original params: " + params

        // ページングのために、max/offsetをセットアップする。
        params.max = params.max?.toInteger() ? Math.min(params.max?.toInteger(), config.irclog.viewer.defaultMax) : config.irclog.viewer.defaultMax
        params.offset = params.offset?.toInteger() ?: 0

        log.debug "Normalized params: " + params
    }

    // MEMO:
    // paginateタグのparams属性でcriterionを渡すと、リクエストのparamsスコープのmax/offset値が
    // params属性で渡したcriterion中のmax/offsetで上書きされてしまい、戻るボタンのページ遷移がおかしくなる。
    // よって、max/offsetはcriterionとして取り扱わない。
    private parseCriterion() {
        def criterion
        // もし、今回検索条件が未指定の場合は、セッション上の検索条件を適用する。
        if (!params.period && session['IRCLOG_VIEWER_CRITERION']) {
            criterion = session['IRCLOG_VIEWER_CRITERION']
            log.debug "Criterion restored from session."
        }
        // 新しくリクエストパラメータからパースする。
        else {
            criterion = [
                period:    params.period ?: 'today',
                channel:   params.channel ?: 'all',
                type:      params.type ?: 'filtered',
                nick:      params.nick,
                message:   params.message
            ]
            if (criterion.period == 'oneday') {
                criterion['period-oneday-date'] = params['period-oneday-date']
            }
            criterion.remove('') // 値が空のものを除外
            log.debug "Criterion parsed from params."
        }
        session['IRCLOG_VIEWER_CRITERION'] = criterion // いったん別の画面から戻ってきた場合などのために、検索条件をセッションに待避する。
        log.debug "Criterion: " + criterion
        criterion
    }

    private getSelectableChannels() {
        def channels = [:]
        channels['all'] = message(code:'mixedViewer.search.channel.all')
        channelService.getAccessibleChannelList(loginUserDomain, params).each { channels[it.name] = it.name }
        channels
    }

    private getNickPersonList() {
        Person.findAll("from Person as p where p.nicks <> '' and p.color <> ''")
    }
}
