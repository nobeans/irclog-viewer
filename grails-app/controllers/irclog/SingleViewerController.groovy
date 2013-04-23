package irclog

import grails.converters.JSON
import grails.plugins.springsecurity.SpringSecurityService
import irclog.search.SearchCriteriaStore

/**
 * IRCログの単独表示モード用コントローラ
 */
class SingleViewerController {

    IrclogSearchService irclogSearchService
    ChannelService channelService
    SpringSecurityService springSecurityService
    SearchCriteriaStore searchCriteriaStore

    def redirectToLatestUrl() {
        render view: "redirectV02"
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
        ]
        render(view: 'index', model: model)
    }

    def relatedDateList() {
        def channel = Channel.findByName(normalizeChannelName(params.channel))
        render channelService.getRelatedDates(normalizeDate(params.date), channel).collectEntries { String key, Date date ->
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
        def searchResult = irclogSearchService.search(springSecurityService.currentUser, criterion, [:], 'asc')

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
        ]
        criterion['periodOnedayDate'] = params.date
        criterion.remove('') // 値が空のものを除外
        log.debug "Criterion: " + criterion
        criterion
    }

    private getSelectableChannels() {
        channelService.getAccessibleChannelList(springSecurityService.currentUser, params).grep { !it.isArchived }.collect { it.name }
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
