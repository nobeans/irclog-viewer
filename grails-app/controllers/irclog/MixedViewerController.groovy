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
        ]
    }

    /**
     * セッション上の検索条件を削除して、リダイレクトする。
     */
    def clearCriteria() {
        searchCriteriaStore.clear()
        redirect(action: 'index')
    }
}
