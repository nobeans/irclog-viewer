// プライベート名前空間としてのグローバルオブジェクトを定義
var IRCLOG;
if (!IRCLOG) IRCLOG = {};

(function() {

    // Initialize
    Event.observe(window, 'load', function() {
        IRCLOG.highlightLine(location.hash.replace('#',''))
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

})();
