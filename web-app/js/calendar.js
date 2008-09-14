(function() {

    YAHOO.namespace("my");
    YAHOO.my.months = ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"];
    YAHOO.my.dayOfWeek = ["日", "月", "火", "水", "木", "金", "土"];
    YAHOO.my.baseId = "period-oneday-date";

    YAHOO.util.Event.addListener(window, "load", initDoc);
    function initDoc() {
        // カレンダの初期化
        YAHOO.my.calendar = new YAHOO.widget.Calendar("calendar", YAHOO.my.baseId + "-calendar", { title:"日付を選択してください", close:true });
        YAHOO.my.calendar.selectEvent.subscribe(handleSelect, YAHOO.my.calendar, true);
        YAHOO.my.calendar.cfg.setProperty("MDY_YEAR_POSITION",  1);
        YAHOO.my.calendar.cfg.setProperty("MDY_MONTH_POSITION", 2);
        YAHOO.my.calendar.cfg.setProperty("MY_YEAR_POSITION",  1);
        YAHOO.my.calendar.cfg.setProperty("MY_MONTH_POSITION", 2);
        YAHOO.my.calendar.cfg.setProperty("MDY_DAY_POSITION",  3);
        YAHOO.my.calendar.cfg.setProperty("MONTHS_SHORT",   YAHOO.my.months);
        YAHOO.my.calendar.cfg.setProperty("MONTHS_LONG",    YAHOO.my.months);
        YAHOO.my.calendar.cfg.setProperty("WEEKDAYS_1CHAR", YAHOO.my.dayOfWeek);
        YAHOO.my.calendar.cfg.setProperty("WEEKDAYS_SHORT", YAHOO.my.dayOfWeek);
        YAHOO.my.calendar.cfg.setProperty("WEEKDAYS_MEDIUM",YAHOO.my.dayOfWeek);
        YAHOO.my.calendar.cfg.setProperty("WEEKDAYS_LONG",  YAHOO.my.dayOfWeek);
        YAHOO.my.calendar.render();

        // カレンダ表示ボタンの初期化
        YAHOO.util.Event.addListener(YAHOO.my.baseId + "-button", "click", handleFocus);
    }               

    function handleFocus() {
        // カレンダの表示位置をボタンのちょうど下の部分にする。
        var button = $(YAHOO.my.baseId + "-button");
        var buttonOffset = Position.cumulativeOffset(button);
        var calendar = $(YAHOO.my.baseId + '-calendar');
        calendar.style.left = buttonOffset[0] - button.getWidth();
        calendar.style.top += '24px';

        // 表示する。
        YAHOO.my.calendar.show();
    }               

    function handleSelect(type, args, obj) {
        var date = args[0];
        var date = date[0];
        var year = date[0], month = date[1], day = date[2];
        var textField = document.getElementById(YAHOO.my.baseId + "-text");
        textField.value = year + "-" + month + "-" + day;
        YAHOO.my.calendar.hide();
    }

})()
