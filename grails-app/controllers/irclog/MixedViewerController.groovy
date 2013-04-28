package irclog

import irclog.search.SearchCriteriaStore
import irclog.search.SearchQuery

class MixedViewerController {

    SearchCriteriaStore searchCriteriaStore

    def redirectToLatestUrl() {
        redirect action: "index", params: params
    }

    def index(SearchQuery query) {
        def searchResult = query.search(searchCriteriaStore)
        return [
            query: query,
            criteriaMap: query.toMap(),
            irclogList: searchResult.list,
            irclogTotalCount: searchResult.totalCount,
            essentialTypes: Irclog.ESSENTIAL_TYPES,
        ]
    }

    def clearCriteria() {
        searchCriteriaStore.clear()
        redirect(action: 'index')
    }
}
