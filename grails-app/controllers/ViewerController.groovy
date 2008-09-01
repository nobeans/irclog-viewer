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
        def query = searchService.createQuery(criterion)
        if (query.message) flash.message = query.message
        [
            irclogList: searchService.search(query, [max:params.max, offset:params.offset]),
            irclogCount: searchService.count(query),
            selectableChannels: searchService.getSelectableChannels(),
            selectableScopes: searchService.getSelectableScopes(),
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
