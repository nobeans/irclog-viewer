package irclog

import irclog.search.SearchCriteriaStore
import irclog.search.SearchQuery

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
    def index(SearchQuery query) {
        def searchResult = query.search(searchCriteriaStore)
        def nickPersonList = Person.list()
        return [
            query: query,
            criteriaMap: query.toMap(),
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
