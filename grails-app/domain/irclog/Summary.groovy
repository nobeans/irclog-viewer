package irclog

import groovy.transform.ToString

/**
 * Summary for each channel
 */
@ToString
class Summary {

    Channel channel
    Date lastUpdated  // = origin = today

    // Counts
    Integer today         // = origin
    Integer yesterday
    Integer twoDaysAgo
    Integer threeDaysAgo
    Integer fourDaysAgo
    Integer fiveDaysAgo
    Integer sixDaysAgo
    Integer totalBeforeYesterday  // total of all count of past days except today

    Irclog latestIrclog

    static final SORTABLE = [
        'channel',
        'lastUpdated',
        'today',
        'yesterday',
        'twoDaysAgo',
        'threeDaysAgo',
        'fourDaysAgo',
        'fiveDaysAgo',
        'sixDaysAgo',
        'totalBeforeYesterday',
        'latestIrclog'
    ]

    static belongsTo = Channel

    static constraints = {
        channel unique: true
        lastUpdated()
        today()
        yesterday()
        twoDaysAgo()
        threeDaysAgo()
        fourDaysAgo()
        fiveDaysAgo()
        sixDaysAgo()
        totalBeforeYesterday()
        latestIrclog()
    }

    static mapping = {
        version(false)
        today(column: 'today_')
    }

    int total() {
        return today + totalBeforeYesterday
    }
}
