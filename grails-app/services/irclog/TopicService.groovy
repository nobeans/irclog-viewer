package irclog

import irclog.utils.DateUtils
import java.text.SimpleDateFormat

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
                ge('time', DateUtils.today - 7)
            }
            maxResults(5)
        }
    }
}
