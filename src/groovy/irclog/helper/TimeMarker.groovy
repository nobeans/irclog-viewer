package irclog.helper

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
        return Date.parse("yyyy-MM-dd HH:mm", new Date().format("yyyy-MM-dd ${time}"))
    }

    String toString() {
        return time
    }
}
