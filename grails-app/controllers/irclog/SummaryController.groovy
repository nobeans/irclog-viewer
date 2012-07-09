package irclog

class SummaryController {

    def channelService
    def summaryListService
    def topicService

    def index() {
        def channelList = channelService.getAccessibleChannelList(request.loginUserDomain, [:]).grep{!it.isArchived}
        [
            summaryList: summaryListService.getSummaryList(params, channelList),
            nickPersonList: Person.list(),
            topicList: topicService.getHotTopicList(channelList)
        ]
    }
}
