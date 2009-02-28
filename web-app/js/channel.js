// プライベート名前空間としてのグローバルオブジェクトを定義
var IRCLOG;
if (!IRCLOG) IRCLOG = {};

(function() {

    IRCLOG.toggleJoinToSecretChannel = function() {
        var button = $('join');
        button.toggleClassName('active');

        var pane = $('joinToSecretChannel');
        pane.style.left = button.offsetLeft + 'px';
        pane.toggle();

        // 非表示でなければ、フォーカスを当てる。
        if (pane.style.display !== 'none') {
            $('channelName').focus();
        }
    }

})()
