package irclog

class SummaryController {

    def channelService
    def topicService

    def index() {
        def channelList = channelService.getAccessibleChannelList(authenticatedUser, [:]).grep { !it.isArchived }
        [
            channelList: channelList,
            nickPersonList: Person.list(),
            topicList: topicService.getHotTopicList(channelList)
        ]
    }
}
