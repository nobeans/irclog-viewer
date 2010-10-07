// プライベート名前空間としてのグローバルオブジェクトを定義
var IRCLOG;
if (!IRCLOG) IRCLOG = {};

(function() {

    // Initialize
    Event.observe(window, 'load', function() {
        IRCLOG.highlightLine(location.hash.replace('#',''));
        setupCalendar();
    });

    // 1行すべてハイライトする。
    // 対象要素が存在しないIDを指定した場合は、ハイライトを解除する。
    IRCLOG.highlightLine = (function () {
        var currentElement = null;
        return function (newElementId) {
            // ハイライト行の世代交代をする。
            var newElement = $(newElementId);
            if (currentElement) currentElement.removeClassName('highlight');
            if (newElement) newElement.addClassName('highlight');
            currentElement = newElement;

            // ハイライト状態の時だけ、選択解除ボタンを有効にする。
            $('clearHighlight').disabled = (!newElement);
        };
    })();

    // 基本種別のログのみを表示するモードとその他の種別も表示するモードをトグルで切り替える。
    // 初期状態：すべて表示
    //
    // すべての種別を表示する。
    IRCLOG.showAllType = (function () {
        var showIt = function (it) { it.show() }
        return function () {
            $$('tr.optionType').each(showIt);
            $('toggleTypeFilter-all').hide();
            $('toggleTypeFilter-filtered').show();
        };
    })();
    // 基本種別のみを表示する。
    IRCLOG.hideControlType = (function () {
        var hideIt = function (it) { it.hide() };
        return function () {
            $$('tr.optionType').each(hideIt);
            $('toggleTypeFilter-all').show();
            $('toggleTypeFilter-filtered').hide();
        };
    })();

    // カレンダ設定
    var setupCalendar = function() {
        var baseId = "singleCalendar";
        var calendarId = baseId + "-calendar"
        var buttonId = baseId + "-button"

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

        var calendar = IRCLOG.createCalendar(calendarId);
        Event.observe(buttonId, "click",
            IRCLOG.createCalendarToggleHandler(calendar, function() { return $('currentDate').innerHTML }, $(buttonId))
        );
        calendar.selectEvent.subscribe(createHandleSelect(calendar), calendar, true);
    }

})();
