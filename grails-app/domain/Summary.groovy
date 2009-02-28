/**
 * チャンネルごとのサマリ情報
 */
class Summary {

    Channel channel
    Date baseDate     // 基準日(＝今日)

    // 各種カウント
    int today         // 今日(＝基準日)
    int yesterday     // 昨日
    int twoDaysAgo    // 2日前
    int threeDaysAgo  // 3日前
    int fourDaysAgo   // 4日前
    int fiveDaysAgo   // 5日前
    int sixDaysAgo    // 6日前
    int totalBeforeYesterday  // 今日をのぞく過去全ての合計

    Date latestTime   // 最新発言日時

    public int total() {
        return today + totalBeforeYesterday
    }

    public String toString() {
        def df = new java.text.SimpleDateFormat("yyyy-MM-dd")
        return """${channel?.name}@${df.format(baseDate)} {
            today:${today}
            yesterday:${yesterday}
            twoDaysAgo:${twoDaysAgo}
            threeDaysAgo:${threeDaysAgo}
            fourDaysAgo:${fourDaysAgo}
            fiveDaysAgo:${fiveDaysAgo}
            sixDaysAgo:${sixDaysAgo}
            today:${today}
            latestTime:${latestTime}
        }"""
    }

    static belongsTo = Channel
    static mapping = {
        version(false)
        message(type:'text')
    }
}
