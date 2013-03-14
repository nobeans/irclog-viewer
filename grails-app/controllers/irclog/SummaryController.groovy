package irclog

class SummaryController {

    def channelService
    def summaryListService
    def topicService
    def springSecurityService

    def index() {
        def channelList = channelService.getAccessibleChannelList(authenticatedUser, [:]).grep { !it.isArchived }
        [
            summaryList: summaryListService.getSummaryList(params, channelList),
            nickPersonList: Person.list(),
            topicList: topicService.getHotTopicList(channelList)
        ]
    }
}
