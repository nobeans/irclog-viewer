package irclog

import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * IRCログのミックス表示モード用コントローラ。
 */
class MixedViewerController {

    def irclogSearchService
    def channelService

    final SELECTABLE_PERIODS = [ 'all', 'year', 'halfyear', 'month', 'week', 'today', 'oneday' ]
    static final String SESSION_KEY_CRITERION = 'criterion'

    /**
     * ログ一覧を表示する。
     */
    def index() {
        // パラメータを正規化する。
        normalizeParams()

        // 検索条件をパースする。
        def criterion = parseCriterion()

        // ログ一覧を取得する。
        def searchResult = irclogSearchService.search(request.loginUserDomain, criterion, [max:params.max, offset:params.offset])
        flash.message = null

        // モデルを作成して、デフォルトビューへ。
        def nickPersonList = Person.list()
        def model = [
            irclogList: searchResult.list,
            essentialTypes: Irclog.ESSENTIAL_TYPES,
            irclogCount: searchResult.totalCount,
            selectableChannels: getSelectableChannels(),
            selectablePeriods: SELECTABLE_PERIODS,
            criterion: criterion,
            nickPersonList: nickPersonList,
            getPersonByNick: createGetPersonByNickClosure(nickPersonList)
        ]
        render(view:'index', model:model)
    }

    /** 
     * セッション上の検索条件を削除して、リダイレクトする。
     */
    def clearCriterion() {
        session.removeAttribute(SESSION_KEY_CRITERION)
        redirect(action:'index')
    }

    private normalizeParams() {
        log.debug "Original params: " + params

        // ページングのために、max/offsetをセットアップする。
        def defaultMax = grailsApplication.config.irclog.viewer.defaultMax
        params.max = params.max?.toInteger() ? Math.min(params.max?.toInteger(), defaultMax) : defaultMax
        params.offset = params.offset?.toInteger() ?: 0

        log.debug "Normalized params: " + params
    }

    // MEMO:
    // paginateタグのparams属性でcriterionを渡すと、リクエストのparamsスコープのmax/offset値が
    // params属性で渡したcriterion中のmax/offsetで上書きされてしまい、戻るボタンのページ遷移がおかしくなる。
    // よって、max/offsetはcriterionとして取り扱わない。
    private parseCriterion() {
        // もし、今回検索条件が未指定の場合は、セッション上の検索条件を適用する。
        if (!params.period && session[SESSION_KEY_CRITERION]) {
            log.debug "Criterion restored from session."
            return session[SESSION_KEY_CRITERION]
        }

        // 新しくリクエストパラメータからパースする。
        log.debug "Criterion parsed from params."
        def criterion = [
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

        // いったん別の画面から戻ってきた場合などのために、検索条件をセッションに待避する。
        session[SESSION_KEY_CRITERION] = criterion

        log.debug "Criterion: " + criterion
        return criterion
    }

    private getSelectableChannels() {
        def channels = [:]
        channels['all'] = message(code:'mixedViewer.search.channel.all')
        channelService.getAccessibleChannelList(request.loginUserDomain, params).grep{ !it.isArchived }.each{ channels[it.name] = it.name }
        channels
    }

    private createGetPersonByNickClosure(nickPersonList) {
        def cache = [:] // ↓で作られるクロージャに対するグローバル的な変数
        return { nick ->
            if (cache.containsKey(nick)) {
                return cache[nick]
            } else {
                def person = nickPersonList.find{ (it.nicks.split(/\s+/) as List).contains(nick) }
                cache[nick] = person
                return person
            }
        }
    }
}
