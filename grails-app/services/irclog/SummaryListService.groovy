package irclog

class SummaryListService {

    static transactional = false

    List<Summary> getSummaryList(params, channelList) {
        resolveSortCondition(params)
        def summaryList = Summary.findAllByChannelInList(channelList, params)

        // If there is channel which has no summary, return a empty summary
        // as last entry of the list. It's not affected by sort condition.
        def appendList = getNotExistedSummaryList(channelList, summaryList)

        return summaryList + appendList
    }

    private getNotExistedSummaryList(channelList, summaryList) {
        return (channelList - summaryList*.channel).sort {
            it.name
        }.collect { channel ->
            new Summary(channel: channel)
        }
    }

    private resolveSortCondition(params) {
        if (params.sort == null || !params.order in ['asc', 'desc']) {
            params.sort = 'channel.name'
            params.order = 'desc'
        }
    }
}
