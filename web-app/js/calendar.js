(function() {

    YAHOO.namespace("my");
    YAHOO.my.months = ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"];
    YAHOO.my.dayOfWeek = ["日", "月", "火", "水", "木", "金", "土"];
    YAHOO.my.baseId = "period-oneday-date";
    YAHOO.my.calendarId = YAHOO.my.baseId + "-calendar"
    YAHOO.my.buttonId = YAHOO.my.baseId + "-button"
    YAHOO.my.textId = YAHOO.my.baseId + "-text"

    YAHOO.util.Event.addListener(window, "load", initDoc);
    function initDoc() {
        // カレンダの初期化
        initCalendar();

        // カレンダ表示ボタンの初期化
        YAHOO.util.Event.addListener(YAHOO.my.buttonId, "click", handleFocus);
    }
    function initCalendar() {
        var calendar = new YAHOO.widget.Calendar("calendar", YAHOO.my.calendarId, { title:"日付を選択してください", close:true });
        calendar.selectEvent.subscribe(handleSelect, YAHOO.my.calendar, true);
        calendar.cfg.setProperty("MDY_YEAR_POSITION",  1);
        calendar.cfg.setProperty("MDY_MONTH_POSITION", 2);
        calendar.cfg.setProperty("MY_YEAR_POSITION",  1);
        calendar.cfg.setProperty("MY_MONTH_POSITION", 2);
        calendar.cfg.setProperty("MDY_DAY_POSITION",  3);
        calendar.cfg.setProperty("MONTHS_SHORT",   YAHOO.my.months);
        calendar.cfg.setProperty("MONTHS_LONG",    YAHOO.my.months);
        calendar.cfg.setProperty("WEEKDAYS_1CHAR", YAHOO.my.dayOfWeek);
        calendar.cfg.setProperty("WEEKDAYS_SHORT", YAHOO.my.dayOfWeek);
        calendar.cfg.setProperty("WEEKDAYS_MEDIUM",YAHOO.my.dayOfWeek);
        calendar.cfg.setProperty("WEEKDAYS_LONG",  YAHOO.my.dayOfWeek);
        calendar.cfg.setProperty("START_WEEKDAY", 1); // 月曜開始
        YAHOO.my.calendar = calendar;
    }

    function handleFocus() {
        var calendar = YAHOO.my.calendar;

        // 入力済みの日付を取得
        var textField = document.getElementById(YAHOO.my.textId);
        var val = textField.value.split('-');
        var y = parseInt(val[0]), m = parseInt(val[1]), d = parseInt(val[2]);
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
        $(YAHOO.my.calendarId).style.left = (textField.offsetLeft) + "px";

        // 表示する。
        calendar.show();
    }               

    function handleSelect(type, args, obj) {
        var date = args[0];
        var date = date[0];
        var year = date[0], month = date[1], day = date[2];
        var textField = document.getElementById(YAHOO.my.textId);
        textField.value = year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
        YAHOO.my.calendar.hide();
    }

})()
