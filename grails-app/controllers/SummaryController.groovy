class SummaryController extends Base {

    def summaryService
    def channelService
    
    def index = {
        // using TimerMarker
        if (session.timeMarker) params.timeMarker = session.timeMarker.call()

        def channelList = channelService.getAccessibleChannelList(loginUserDomain, [:])
        [
            summaryList: summaryService.getAccessibleSummaryList(params, channelList),
            nickPersonList: Person.list(),
            topicList: summaryService.getAccessibleTopicList(loginUserDomain, channelList)
        ]
    }

}
