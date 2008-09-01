/**
 * IRCログの表示処理のコントローラ。
 */
class ViewerController {

    def searchService
    
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
        def searchResult = searchService.search(criterion, [max:params.max, offset:params.offset])
        if (searchResult.message) flash.message = searchResult.message
println searchResult
        [
            irclogList: searchResult.list,
            irclogCount: searchResult.totalCount,
            selectableChannels: getSelectableChannels(),
            selectableScopes: getSelectableScopes(),
            criterion: criterion
        ]
    }

    // paginateタグのparams属性でcriterionを渡すと、リクエストのparamsスコープのmax/offset値が
    // params属性で渡したcriterion中のmax/offsetで上書きされてしまい、戻るボタンのページ遷移がおかしくなる。
    // よって、max/offsetはcriterionとして取り扱わない。
    private parseCriterion(params) {
        def criterion = [
            scope:     params.scope,
            channelId: params.channelId,
            nick:      params.nick,
            message:   params.message
        ]
        if (criterion.scope == 'specified') {
            criterion['scope-specified-date'] = params['scope-specified-date']
        }
        criterion
    }

    private getSelectableChannels() {
        def channels = [:]
        channels[''] = '未指定'
        searchService.getAccessableChannels().each { channels[it.id] = '#' + it.name }
        channels['all'] = 'すべて'
        channels
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
