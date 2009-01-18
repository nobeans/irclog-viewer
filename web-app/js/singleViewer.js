// Public Functions
var focusMessage;

(function() {

    // Initialize
    Event.observe(window, 'load', function() {
        focusMessage(location.hash.replace('#',''))
    });

    var currentMessageId = null;

    focusMessage = function (newMessageId) {
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

})()
