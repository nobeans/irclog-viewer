package irclog

import irclog.utils.DateUtils

class TopicService {

    static transactional = false

    /**
     * Top 5 topics within a week in accessible channels are returned.
     * @param accessibleChannelList one instance of Channel or list
     * @return list of hot topics
     */
    List<Irclog> getHotTopicList(accessibleChannelList) {
        if (!accessibleChannelList) return []
        return Irclog.withCriteria {
            and {
                'in'('channel', accessibleChannelList)
                eq('type', 'TOPIC')
                ge('time', getToday() - 7)
            }
            maxResults(5)
            order('time', 'desc')
        }
    }

    private getToday = { DateUtils.today }
}
