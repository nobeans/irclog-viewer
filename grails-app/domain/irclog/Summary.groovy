package irclog

/**
 * Summary for each channel
 */
class Summary {

    Channel channel
    Date lastUpdated  // = origin = today

    // Counts
    int today         // ＝origin
    int yesterday
    int twoDaysAgo
    int threeDaysAgo
    int fourDaysAgo
    int fiveDaysAgo
    int sixDaysAgo
    int totalBeforeYesterday  // total of all count of past days except today

    Irclog latestIrclog

    int total() {
        return today + totalBeforeYesterday
    }

    String toString() {
        def df = new java.text.SimpleDateFormat("yyyy-MM-dd")
        return """${channel?.name}@${lastUpdated ? df.format(lastUpdated) : 'NEVER'} {
                 |    today: ${today}
                 |    yesterday: ${yesterday}
                 |    twoDaysAgo: ${twoDaysAgo}
                 |    threeDaysAgo: ${threeDaysAgo}
                 |    fourDaysAgo: ${fourDaysAgo}
                 |    fiveDaysAgo: ${fiveDaysAgo}
                 |    sixDaysAgo: ${sixDaysAgo}
                 |    totalBeforeYesterday: ${totalBeforeYesterday}
                 |    latestIrclog: ${latestIrclog}
                 |}""".stripMargin()
    }

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

    static mapping = {
        version(false)
        message(type:'text')
        today(column:'today_')
    }
}
