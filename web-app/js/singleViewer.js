// プライベート名前空間としてのグローバルオブジェクトを定義
var IRCLOG;
if (!IRCLOG) IRCLOG = {};

(function() {

    // Initialize
    Event.observe(window, 'load', function() {
        IRCLOG.focusMessage(location.hash.replace('#',''))
    });

    var currentMessageId = null;

    IRCLOG.focusMessage = function (newMessageId) {
        removeStyleClass(currentMessageId, 'this');
        addStyleClass(newMessageId, 'this');
        currentMessageId = newMessageId;
    }

    function addStyleClass(elementId, className) {
        var ele = $(elementId);
        if (ele) ele.addClassName(className);
    }

    function removeStyleClass(elementId, className) {
        var ele = $(elementId);
        if (ele) ele.removeClassName(className);
    }

})();
