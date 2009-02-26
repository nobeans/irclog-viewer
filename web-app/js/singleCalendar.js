// プライベート名前空間としてのグローバルオブジェクトを定義
var IRCLOG;
if (!IRCLOG) IRCLOG = {};

(function() {

    var baseId = "singleCalendar";
    var calendarId = baseId + "-calendar"
    var buttonId = baseId + "-button"

    // 初期化
    Event.observe(window, "load", function() {
        var calendar = IRCLOG.createCalendar(calendarId);
        Event.observe(buttonId, "click",
            IRCLOG.createCalendarToggleHandler(calendar, function() { return $('currentDate').innerHTML }, $(buttonId))
        );
        calendar.selectEvent.subscribe(createHandleSelect(calendar), calendar, true);
    });

    var createHandleSelect = function(calendar) {
        return function(type, args, obj) {
            // 日付部を取得する。
            var date = args[0];
            var date = date[0];
            var year = date[0], month = date[1], day = date[2];
            var targetDate = "" + year + (month < 10 ? "0" + month : month) + (day < 10 ? "0" + day : day);

            // チャンネルを取得する。
            var select = $('select-single');
            var channel = select[select.selectedIndex].value.replace('#', '');

            // URLを作成して、リダイレクトする。
            var url = '/irclog/the/' + channel + '/' + targetDate + '/';
            //if (!confirm(url)) return false;
            document.location = url;
        }
    }

})()
