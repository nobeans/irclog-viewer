package irclog

class SummaryController {

    def channelService
    def summaryService
    def topicService

    def index() {
        def channelList = channelService.getAccessibleChannelList(request.loginUserDomain, [:]).grep{!it.isArchived}
        [
            summaryList: summaryService.getAccessibleSummaryList(params, channelList),
            nickPersonList: Person.list(),
            topicList: topicService.getHotTopicList(channelList)
        ]
    }
}
