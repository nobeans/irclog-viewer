(function() {

    YAHOO.namespace("my");
    YAHOO.util.Event.addListener(window, "load", initDoc);

    function initDoc() {
        YAHOO.my.textFieldElementId = document.getElementById("yui-calendar").firstChild.nodeValue;

        YAHOO.my.calendar = new YAHOO.widget.Calendar("calendar", "yui-calendar", { title:"日付を選択してください", close:true } );
        YAHOO.my.calendar.selectEvent.subscribe(handleSelect, YAHOO.my.calendar, true);

        YAHOO.my.calendar.cfg.setProperty("MDY_YEAR_POSITION", 1);
        YAHOO.my.calendar.cfg.setProperty("MDY_MONTH_POSITION", 2);
        YAHOO.my.calendar.cfg.setProperty("MDY_DAY_POSITION", 3);

        YAHOO.my.calendar.cfg.setProperty("MY_YEAR_POSITION", 1);
        YAHOO.my.calendar.cfg.setProperty("MY_MONTH_POSITION", 2);

        YAHOO.my.calendar.cfg.setProperty("MONTHS_SHORT",   ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"]);
        YAHOO.my.calendar.cfg.setProperty("MONTHS_LONG",    ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"]);
        YAHOO.my.calendar.cfg.setProperty("WEEKDAYS_1CHAR", ["日", "月", "火", "水", "木", "金", "土"]);
        YAHOO.my.calendar.cfg.setProperty("WEEKDAYS_SHORT", ["日","月", "火", "水", "木", "金", "土"]);
        YAHOO.my.calendar.cfg.setProperty("WEEKDAYS_MEDIUM",["日","月", "火", "水", "木", "金", "土"]);
        YAHOO.my.calendar.cfg.setProperty("WEEKDAYS_LONG",  ["日","月", "火", "水", "木", "金", "土"]);

        YAHOO.my.calendar.render();
        YAHOO.util.Event.addListener(YAHOO.my.textFieldElementId, "focus", handleFocus, YAHOO.my.calendar, true);
    }               

    function handleFocus(type, args, obj) {
      YAHOO.my.calendar.show();
    }               

    function handleSelect(type, args, obj) {
        var dates = args[0]; 
        var date = dates[0];
        var year = date[0], month = date[1], day = date[2];

      var txtDate = document.getElementById(YAHOO.my.textFieldElementId);
      txtDate.value = year + "-" + month + "-" + day;
      YAHOO.my.calendar.hide();
    }

})()
