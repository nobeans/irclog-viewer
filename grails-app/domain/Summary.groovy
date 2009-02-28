/**
 * チャンネルごとのサマリ情報
 */
class Summary {

    Channel channel
    Date lastUpdated  // 最終更新日(＝基準日＝今日)

    // 各種カウント
    int today         // 今日(＝基準日)
    int yesterday     // 昨日
    int twoDaysAgo    // 2日前
    int threeDaysAgo  // 3日前
    int fourDaysAgo   // 4日前
    int fiveDaysAgo   // 5日前
    int sixDaysAgo    // 6日前
    int totalBeforeYesterday  // 今日をのぞく過去全ての合計

    Irclog latestIrclog     // 最新発言ログ

    public int total() {
        return today + totalBeforeYesterday
    }

    public String toString() {
        def df = new java.text.SimpleDateFormat("yyyy-MM-dd")
        return """${channel?.name}@${lastUpdated ? df.format(lastUpdated) : 'NEVER'} {
    today: ${today}
    yesterday: ${yesterday}
    twoDaysAgo: ${twoDaysAgo}
    threeDaysAgo: ${threeDaysAgo}
    fourDaysAgo: ${fourDaysAgo}
    fiveDaysAgo: ${fiveDaysAgo}
    sixDaysAgo: ${sixDaysAgo}
    totalBeforeYesterday: ${totalBeforeYesterday}
    latestIrclog: ${latestIrclog}
}"""
    }

    public static final SORTABLE = [
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
    }
}
