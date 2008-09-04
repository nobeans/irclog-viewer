/**
 * IRCログの表示処理のコントローラ。
 */
class ViewerController {

    def irclogSearchService
    def channelService
    
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
        def searchResult = irclogSearchService.search(criterion, [max:params.max, offset:params.offset])
        if (searchResult.message) flash.message = searchResult.message
        [
            irclogList: searchResult.list,
            irclogCount: searchResult.totalCount,
            selectableChannels: getSelectableChannels(),
            selectablePeriods: getSelectablePeriods(),
            criterion: criterion
        ]
    }

    // paginateタグのparams属性でcriterionを渡すと、リクエストのparamsスコープのmax/offset値が
    // params属性で渡したcriterion中のmax/offsetで上書きされてしまい、戻るボタンのページ遷移がおかしくなる。
    // よって、max/offsetはcriterionとして取り扱わない。
    private parseCriterion(params) {
        def criterion = [
            period:     params.period ?: 'all',
            channelId: params.channelId ?: 'all',
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
        channels['all'] = 'すべて'
        channelService.getAccessableChannels().each { channels[it.id] = it.name }
        channels
    }

    private getSelectablePeriods() {[
        hour:'1時間以内', today:'今日のみ', oneday:'指定日...', week:'1週間以内', month:'1ヶ月以内', year:'1年以内', all:'すべて'
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
