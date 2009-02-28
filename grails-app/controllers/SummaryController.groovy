class SummaryController extends Base {

    def summaryService
    def channelService
    
    def index = {
        def channelList = channelService.getAccessibleChannelList(loginUserDomain, [:])
        [
            summaryList: summaryService.getAccessibleSummaryList(params, channelList),
            nickPersonList: Person.list(),
            topicList: summaryService.getAccessibleTopicList(loginUserDomain)
        ]
    }

}
