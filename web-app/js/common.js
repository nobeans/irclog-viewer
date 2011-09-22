// プライベート名前空間としてのグローバルオブジェクトを定義
var IRCLOG;
if (!IRCLOG) IRCLOG = {};

jQuery(function($) {

    // 指定されたURLに遷移する。
    IRCLOG.goto = function(url) {
        document.location = url;
    }

    // Help caption
    $(".help-button").click(function() {
        $("#" + this.id + "-caption").toggle();
    });

    // Default focus
    $(".defaultFocus").focus();
});
