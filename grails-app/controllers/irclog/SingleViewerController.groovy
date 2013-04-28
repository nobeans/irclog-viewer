package irclog

import grails.converters.JSON
import irclog.search.DetailQuery

class SingleViewerController {

    def redirectToLatestUrl() {
        render view: "redirectV02"
    }

    def index(DetailQuery query) {
        return [
            criterion: query.toMap(),
            essentialTypes: Irclog.ESSENTIAL_TYPES,
        ]
    }

    def relatedDateList(DetailQuery query) {
        render query.relatedDateList as JSON
    }

    def channelList(DetailQuery query) {
        render query.channelCandidates as JSON
    }

    def irclogList(DetailQuery query) {
        render query.irclogList.collect { Irclog irclog ->
            irclog.properties["type", "message", "nick", "permaId", "channelName"] + [time: irclog.time.format('HH:mm:ss')]
        } as JSON
    }
}
