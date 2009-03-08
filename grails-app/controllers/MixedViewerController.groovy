import java.text.SimpleDateFormat
import java.text.ParseException

/**
 * IRCログのミックス表示モード用コントローラ。
 */
class MixedViewerController extends Base {

    def irclogSearchService
    def channelService

    final SELECTABLE_PERIODS = ['all', 'year', 'month', 'week', 'today', 'oneday']
    final SESSION_KEY_CRITERION = 'irclog.MixedViewerController.CRITERION'
    
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
        def nickPersonList = Person.list()
        def model = [
            irclogList: searchResult.list,
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
    def clearCriterion = {
        session.removeAttribute(SESSION_KEY_CRITERION)
        redirect(action:index)
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

        // タイムマーカがある場合はセッションに格納する。
        // FIXME:日付パースの部分はUtil化する。
        if (criterion.period == 'today') {
            if (params['period-today-time'] ==~ /([01]?[0-9]|2[0-3]):([0-5]?[0-9])/) {
                try {
                    // 評価時の日付を元にタイムマーカ日時を決定するクロージャ
                    session.timeMarker = new TimeMarker(params['period-today-time'])
                    def time = session.timeMarker.time
                    criterion['period-today-time'] = new SimpleDateFormat('HH:mm').format(time) // for View
                    criterion['period-today-time-object'] = time // for Service FIXME:Serviceで別途Utilを使ってDate化する方向で整理
                } catch (ParseException e) {
                    flash.errors = ['mixedViewer.search.timeWorker.error']
                }
            }
        }
        if (criterion['period-today-time'] == null) {
            session.removeAttribute('timeMarker')
        }

        // いったん別の画面から戻ってきた場合などのために、検索条件をセッションに待避する。
        session[SESSION_KEY_CRITERION] = criterion

        log.debug "Criterion: " + criterion
        return criterion
    }

    private getSelectableChannels() {
        def channels = [:]
        channels['all'] = message(code:'mixedViewer.search.channel.all')
        channelService.getAccessibleChannelList(loginUserDomain, params).each { channels[it.name] = it.name }
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
