class SummaryController extends Base {

    def summaryService
    def channelService
    
    def index = {
        [
            summaryList: summaryService.getAllSummaryList(),
            channelList: channelService.getAccessibleChannelList(loginUserDomain, params),
            nickPersonList: Person.list()
        ]
    }

}
