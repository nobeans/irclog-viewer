// プライベート名前空間としてのグローバルオブジェクトを定義
var IRCLOG;
if (!IRCLOG) IRCLOG = {};

(function() {

    var months = ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"];
    var dayOfWeek = ["日", "月", "火", "水", "木", "金", "土"];

    // カレンダオブジェクトを生成する。
    IRCLOG.createCalendar = function(calendarContainer) {
        calendarContainer = $(calendarContainer);
        var calendar = new YAHOO.widget.Calendar("calendar", calendarContainer, { title:"日付を選択してください", close:true });
        calendar.cfg.setProperty("MDY_YEAR_POSITION",  1);
        calendar.cfg.setProperty("MDY_MONTH_POSITION", 2);
        calendar.cfg.setProperty("MY_YEAR_POSITION",   1);
        calendar.cfg.setProperty("MY_MONTH_POSITION",  2);
        calendar.cfg.setProperty("MDY_DAY_POSITION",   3);
        calendar.cfg.setProperty("MONTHS_SHORT",       months);
        calendar.cfg.setProperty("MONTHS_LONG",        months);
        calendar.cfg.setProperty("WEEKDAYS_1CHAR",     dayOfWeek);
        calendar.cfg.setProperty("WEEKDAYS_SHORT",     dayOfWeek);
        calendar.cfg.setProperty("WEEKDAYS_MEDIUM",    dayOfWeek);
        calendar.cfg.setProperty("WEEKDAYS_LONG",      dayOfWeek);
        calendar.cfg.setProperty("START_WEEKDAY",      1); // 月曜開始
        calendar.container = calendarContainer; // 包含要素を設定しておく(独自)
        return calendar;
    }

    // カレンダを表示/非表示するハンドラを生成する。
    // @param calendar 対象カレンダ
    // @param getDateValueFunc YYYY-MM-DDの日付文字列を取得する関数。実行時まで評価遅延させるために関数とした。
    // @param displayBasement カレンダ表示の基準要素
    IRCLOG.createCalendarToggleHandler = function(calendar, getDateValueFunc, displayBasement) {
        var prepareOfShow = function() {
            // 入力済みの日付を取得
            var val = getDateValueFunc().split('-');
            var y = parseInt(val[0], 10), m = parseInt(val[1], 10), d = parseInt(val[2], 10);
            var shown = new Date(y, m - 1, d);

            // 表示設定
            var pagedate = "", selected = "";
            if (shown.getFullYear() == y && shown.getMonth() + 1 == m && shown.getDate() == d) {
                //日付として正しい場合
                pagedate = y + "/" + m;
                selected = y + "/" + m + "/" + d;
            }
            calendar.cfg.setProperty("pagedate", pagedate); // 表示する年月
            calendar.cfg.setProperty("selected", selected); // 選択状態の日付
            calendar.render();

            // カレンダの表示位置を日付テキストフィールドの真下にする。
            calendar.container.style.left = ($(displayBasement).offsetLeft) + "px";

            // 表示する。
            calendar.show();
        }

        // トグル対応
        var nextFunc = showIt;
        function hideIt(it) { it.hide(); nextFunc = showIt }
        function showIt(it) { prepareOfShow(); it.show(); nextFunc = hideIt }
        return function () {
            nextFunc(calendar);
        };
    }

})()
