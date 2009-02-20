// プライベート名前空間としてのグローバルオブジェクトを定義
var IRCLOG;
if (!IRCLOG) IRCLOG = {};

(function() {

    IRCLOG.goto = function(url) {
        document.location = url;
    }

    IRCLOG.toggleJoinToSecretChannel = function() {
        var button = $('join');
        button.toggleClassName('active');

        var pane = $('joinToSecretChannel');
        pane.style.left = button.offsetLeft + 'px';
        pane.toggle();

        $('channelName').focus();
    }

})()
