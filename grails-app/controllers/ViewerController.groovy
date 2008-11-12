/**
 * IRCログの表示処理のコントローラ。
 */
class ViewerController extends Base {

    def irclogSearchService
    def channelService

    final DATE_FORMAT = new java.text.SimpleDateFormat('yyyy-MM-dd')
    final SELECTABLE_PERIODS = ['all', 'year', 'month', 'week', 'today', 'oneday']
    
    /**
     * ログ一覧を表示する。
     */
    def index = {
        showViewer(params)
    }

    private showViewer(params) {
        // 検索条件をパースする。
        def criterion = parseCriterion(params)

        // ページングのために、max/offsetをセットアップする。
        params.max = params.max?.toInteger() ?: config.irclog.viewer.defaultMax
        params.offset = params.offset?.toInteger() ?: 0

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
        channels['all'] = message(code:'viewer.search.channel.all')
        channelService.getAccessibleChannelList(loginUserDomain, params).each { channels[it.id] = it.name }
        channels
    }

    /** パーマリンク指定 */
    def specified = {
        def irclog = Irclog.get(params.id)
        if (!irclog) {
            redirect(action:index)
        }
        params.specifiedLogId = irclog.id
        params.channel = irclog.channel.id.toString()
        params.period = 'oneday'
        params['period-oneday-date'] = DATE_FORMAT.format(irclog.time)
        showViewer(params)
    }

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
