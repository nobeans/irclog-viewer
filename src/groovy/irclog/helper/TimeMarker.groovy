package irclog.helper


import java.text.SimpleDateFormat

/**
 * タイムマーカクラス。
 * 今日のログにおいて「ここまで読んだ」を実現するために、時刻境界を表す。
 */
class TimeMarker {

    private String time

    TimeMarker(String time) {
        this.time = time
    }

    /**
     * 指定されたHH:mm文字列を元に「今日」のDateインスタンスを返す。
     * 0時を超えても正常に動作させるため、呼び出し時に遅延評価するところがポイント。
     * Date型であるがあえて文脈に合わせてTimeというプロパティ名とした。
     */
    Date getTime() {
        def today = new SimpleDateFormat('yyyy-MM-dd ').format(new Date())
        return new SimpleDateFormat('yyyy-MM-dd HH:mm').parse(today + time)
    }

    String toString() {
        return time
    }
}
