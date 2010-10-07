package irclog

import irclog.controller.Base

/**
 * IRCログの単独表示モード用コントローラ
 */
class SingleViewerController extends Base {
    
    def irclogSearchService
    def channelService
    
    def index = {
        // パラメータを正規化する。
        normalizeParams()
        
        // 検索条件をパースする。
        def criterion = parseCriterion()
        
        // ログ一覧を取得する。
        def searchResult = irclogSearchService.search(loginUserDomain, criterion, [:], 'asc')
        flash.message = null
        if (searchResult.totalCount == 0) {
            flash.message = 'singleViewer.search.error.empty'
        }
        
        // アクセス可能なチャンネルを取得する。
        def selectableChannels = getSelectableChannels(criterion.channel)
        
        // リンク用の関連日付を取得する。
        def relatedDates = channelService.getRelatedDates(selectableChannels, params.date, Channel.findByName(params.channel), criterion.isIgnoredOptionType)
        
        // モデルを作成して、デフォルトビューへ。
        def nickPersonList = Person.list()
        def model = [
            irclogList: searchResult.list,
            selectableChannels: selectableChannels,
            criterion: criterion,
            relatedDates: relatedDates,
            nickPersonList: nickPersonList,
            getPersonByNick: createGetPersonByNickClosure(nickPersonList),
            needTimerMarkerJump: params.timeMarkerJump
        ]
        render(view:'index', model:model)
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
        date.replaceAll(/(\d{4})(\d{2})(\d{2})/, "\$1-\$2-\$3")
    }
    
    private parseCriterion() {
        def criterion = [
                            period:      'oneday',
                            channel:     normalizeChannelName(params.channel),
                            type:        'all',
                            isIgnoredOptionType: getCurrentTypeInMixed() != 'all'
                        ]
        criterion['period-oneday-date'] = params.date
        criterion.remove('') // 値が空のものを除外
        log.debug "Criterion: " + criterion
        criterion
    }
    
    private getSelectableChannels(specifiedChannel) {
        def channels = [:]
        channels[specifiedChannel] = specifiedChannel // 指定されたチャンネルは必ず表示(書庫対応)
        channelService.getAccessibleChannelList(loginUserDomain, params).grep{!it.isArchived}.each{ channels[it.name] = it.name }
        channels.sort{it.key}
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
                def person = nickPersonList.find{ (it.nicks.split(/\s+/) as List).contains(nick) }
                cache[nick] = person
                return person
            }
        }
    }
}
