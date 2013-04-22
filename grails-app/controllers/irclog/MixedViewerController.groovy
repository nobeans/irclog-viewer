package irclog

import irclog.search.SearchCriteriaCommand
import irclog.search.SearchCriteriaStore

/**
 * IRCログのミックス表示モード用コントローラ。
 */
class MixedViewerController {

    SearchCriteriaStore searchCriteriaStore

    def redirectToLatestUrl() {
        redirect action: "index", params: params
    }

    /**
     * ログ一覧を表示する。
     */
    def index(SearchCriteriaCommand command) {
        def searchResult = command.search(searchCriteriaStore)
        def nickPersonList = Person.list()
        return [
            command: command,
            criteriaMap: command.toMap(),
            irclogList: searchResult.list,
            irclogTotalCount: searchResult.totalCount,
            essentialTypes: Irclog.ESSENTIAL_TYPES,
            nickPersonList: nickPersonList,
            personOfNick: createPersonOfNickClosure(nickPersonList),
        ]
    }

    /**
     * セッション上の検索条件を削除して、リダイレクトする。
     */
    def clearCriteria() {
        searchCriteriaStore.clear()
        redirect(action: 'index')
    }

    // for Coloring of nick
    private static createPersonOfNickClosure(nickPersonList) {
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
