class SummaryController extends Base {

    def summaryService
    def channelService
    
    def index = {
        // FIXME: タイムマーカを生成する。実際はUIで時間を指定する。ダミー。
        params.timeMarker = use( [org.codehaus.groovy.runtime.TimeCategory] ){
            new Date() - 5.minutes
        }

        def channelList = channelService.getAccessibleChannelList(loginUserDomain, [:])
        [
            summaryList: summaryService.getAccessibleSummaryList(params, channelList),
            nickPersonList: Person.list(),
            topicList: summaryService.getAccessibleTopicList(loginUserDomain, channelList)
        ]
    }

}
