package irclog

import groovy.transform.ToString

/**
 * Summary for each channel
 */
@ToString
class Summary {

    Date lastUpdated

    // Counts
    Integer today = 0  // = origin
    Integer yesterday = 0
    Integer twoDaysAgo = 0
    Integer threeDaysAgo = 0
    Integer fourDaysAgo = 0
    Integer fiveDaysAgo = 0
    Integer sixDaysAgo = 0
    Integer totalBeforeYesterday = 0  // total of all count of past days except today
    Integer total = 0

    Irclog latestIrclog
    Channel channel

    static constraints = {
        latestIrclog nullable: true
        channel unique: true
    }

    static mapping = {
        version false
        today column: 'today_'
    }

    def beforeUpdate() {
        total = today + totalBeforeYesterday
    }
}
