import java.text.SimpleDateFormat
import java.text.ParseException

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
        def nickPersonList = Person.list()
        def model = [
            irclogList: searchResult.list,
            irclogCount: searchResult.totalCount,
            selectableChannels: getSelectableChannels(),
            selectablePeriods: SELECTABLE_PERIODS,
            criterion: criterion,
            nickPersonList: nickPersonList,
            getPersonByNick: createGetPersonByNickClosure(nickPersonList),
            timeMarker: session.timeMarker
        ]
        render(view:'index', model:model)
    }

    /** 
     * セッション上の検索条件を削除して、リダイレクトする。
     */
    def clearCriterion = {
        session.removeAttribute('IRCLOG_VIEWER_CRITERION')
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

        // タイムマーカがある場合はセッションに格納する。
        // FIXME:日付パースの部分はUtil化する。
        if (criterion.period == 'today') {
            if (params['period-today-time'] == '') {
                session.removeAttribute('timeMarker')
            } else if (params['period-today-time'] ==~ /([01]?[0-9]|2[0-3]):([0-5]?[0-9])/) {
                def today = new SimpleDateFormat('yyyy-MM-dd ').format(new Date())
                try {
                    session.timeMarker = new SimpleDateFormat('yyyy-MM-dd HH:mm').parse(today + params['period-today-time'])
                    criterion['period-today-time'] = new SimpleDateFormat('HH:mm').format(session.timeMarker) // for View
                    criterion['period-today-time-object'] = session.timeMarker // for Service FIXME:Serviceで別途Utilを使ってDate化する方向で整理
                } catch (ParseException e) {
                    flash.errors = ['mixedViewer.search.timeWorker.error']
                    session.removeAttribute('timeMarker')
                }
            }
        } else {
            session.removeAttribute('timeMarker')
        }

        // いったん別の画面から戻ってきた場合などのために、検索条件をセッションに待避する。
        session['IRCLOG_VIEWER_CRITERION'] = criterion

        log.debug "Criterion: " + criterion
        criterion
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
