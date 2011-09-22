// プライベート名前空間としてのグローバルオブジェクトを定義
var IRCLOG;
if (!IRCLOG) IRCLOG = {};

jQuery(function($) {

    // Help caption
    $(".help-button").click(function() {
        $("#" + this.id + "-caption").toggle();
    });

    // Default focus
    $(".defaultFocus").focus();
});
