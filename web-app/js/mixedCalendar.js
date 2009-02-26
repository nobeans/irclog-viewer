// プライベート名前空間としてのグローバルオブジェクトを定義
var IRCLOG;
if (!IRCLOG) IRCLOG = {};

(function() {

    var baseId = "period-oneday-date";
    var calendarId = baseId + "-calendar";
    var buttonId = baseId + "-button";
    var textId = baseId + "-text";

    // 初期化
    Event.observe(window, "load", function() {
        var calendar = IRCLOG.createCalendar(calendarId);
        Event.observe(buttonId, "click",
            IRCLOG.createCalendarToggleHandler(calendar, function() { return $(textId).value }, $(textId))
        );
        calendar.selectEvent.subscribe(createHandleSelect(calendar), calendar, true);
    });

    var createHandleSelect = function(calendar) {
        return function(type, args, obj) {
            var date = args[0];
            var date = date[0];
            var year = date[0], month = date[1], day = date[2];
            var textField = document.getElementById(textId);
            textField.value = year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
            calendar.hide();
        }
    }

})()
