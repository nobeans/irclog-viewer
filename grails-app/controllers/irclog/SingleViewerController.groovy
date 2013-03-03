package irclog

import grails.converters.JSON

/**
 * IRCログの単独表示モード用コントローラ
 */
class SingleViewerController {

    def irclogSearchService
    def channelService
    def springSecurityService

    def redirectToLatestUrl() {
        redirect action: "index", params: [channel: params.channel, date: normalizeDate(params.date)]
    }

    def index() {
        // パラメータを正規化する。
        normalizeParams()

        // 検索条件をパースする。
        def criterion = parseCriterion()

        // モデルを作成して、デフォルトビューへ。
        def nickPersonList = Person.list()
        def model = [
            essentialTypes: Irclog.ESSENTIAL_TYPES,
            criterion: criterion,
            nickPersonList: nickPersonList,
            getPersonByNick: createGetPersonByNickClosure(nickPersonList),
        ]
        render(view: 'index', model: model)
    }

    def relatedDateList() {
        def isIgnoredOptionType = getCurrentTypeInMixed() != 'all'
        def channel = Channel.findByName(normalizeChannelName(params.channel))
        render channelService.getRelatedDates(normalizeDate(params.date), channel, isIgnoredOptionType).collectEntries { String key, Date date ->
            [key, date?.format('yyyy-MM-dd')]
        } as JSON
    }

    def channelList() {
        render selectableChannels as JSON
    }

    def irclogList() {
        // パラメータを正規化する。
        normalizeParams()

        // 検索条件をパースする。
        def criterion = parseCriterion()

        // ログ一覧を取得する。
        def searchResult = irclogSearchService.search(authenticatedUser, criterion, [:], 'asc')

        render searchResult.list.collect { Irclog irclog ->
            irclog.properties["type", "message", "nick", "permaId", "channelName"] + [time: irclog.time.format('HH:mm:ss')]
        } as JSON
    }

    private normalizeParams() {
        log.debug "Original params: " + params
        params.channel = normalizeChannelName(params.channel)
        params.date = normalizeDate(params.date)
        log.debug "Normalized params: " + params
    }

    private normalizeChannelName(channelName) {
        channelName.startsWith('#') ? channelName : '#' + channelName
    }

    private normalizeDate(date) { // YYYYMMDD -> YYYY-MM-DD
        if (date =~ /\d{8}/) {
            return date.replaceAll(/(\d{4})(\d{2})(\d{2})/, "\$1-\$2-\$3")
        }
        return date
    }

    private parseCriterion() {
        def criterion = [
            period: 'oneday',
            channel: normalizeChannelName(params.channel),
            type: 'all',
            isIgnoredOptionType: getCurrentTypeInMixed() != 'all'
        ]
        criterion['period-oneday-date'] = params.date
        criterion.remove('') // 値が空のものを除外
        log.debug "Criterion: " + criterion
        criterion
    }

    private getSelectableChannels() {
        channelService.getAccessibleChannelList(authenticatedUser, params).grep { !it.isArchived }.collect { it.name }
    }

    /** mixed側での現在のtype条件がallかどうか。*/
    private getCurrentTypeInMixed() {
        session[MixedViewerController.SESSION_KEY_CRITERION]?.type ?: 'filtered'
    }

    private createGetPersonByNickClosure(nickPersonList) {
        def cache = [:] // ↓で作られるクロージャに対するグローバル的な変数
        return { nick ->
            if (cache.containsKey(nick)) {
                return cache[nick]
            } else {
                def person = nickPersonList.find { (it.nicks.split(/\s+/) as List).contains(nick) }
                cache[nick] = person
                return person
            }
        }
    }
}
