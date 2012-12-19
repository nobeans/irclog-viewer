package irclog

class SummaryListService {

    static transactional = false

    List<Summary> getSummaryList(params, channelList) {
        resolveSortCondition(params)
        return Summary.findAllByChannelInList(channelList, params)
    }

    private resolveSortCondition(params) {
        if (params.sort == null || !params.order in ['asc', 'desc']) {
            params.sort = 'channel.name'
            params.order = 'asc'
        }
    }
}
