package irclog

import grails.plugin.cache.Cacheable

class SummaryListService {

    static transactional = false

    List<Summary> getSummaryList(params, channelList) {
        resolveSortCondition(params)
        return findAllSummary(params).findAll { it.channel in channelList }
    }

    @Cacheable("summary")
    private static findAllSummary(params) {
        Summary.list(sort: params.sort, order: params.order)
    }

    private static resolveSortCondition(params) {
        if (params.sort == null || !params.order in ['asc', 'desc']) {
            params.sort = 'channel.name'
            params.order = 'asc'
        }
    }
}
